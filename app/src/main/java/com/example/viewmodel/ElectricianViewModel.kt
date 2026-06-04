package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.model.Calculation
import com.example.model.ElectricalRepository
import com.example.model.QuizQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.PI

sealed class ActiveScreen {
    object Dashboard : ActiveScreen()
    data class CalculatorDetail(val calcIndex: Int, val moduleIndex: Int) : ActiveScreen()
    object QuizSession : ActiveScreen()
}

class ElectricianViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("eletricista_pro_prefs", Context.MODE_PRIVATE)

    // Navigation States
    private val _activeScreen = MutableStateFlow<ActiveScreen>(ActiveScreen.Dashboard)
    val activeScreen: StateFlow<ActiveScreen> = _activeScreen.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Calculadoras, 1: Biblioteca NBR, 2: Simulado
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Interactive Calculator Input Model
    private val _inputs = MutableStateFlow<Map<String, String>>(emptyMap())
    val inputs: StateFlow<Map<String, String>> = _inputs.asStateFlow()

    private val _calculationResult = MutableStateFlow<CalculationOutput?>(null)
    val calculationResult: StateFlow<CalculationOutput?> = _calculationResult.asStateFlow()

    // Favorites List
    private val _favorites = MutableStateFlow<List<FavoriteCalculation>>(emptyList())
    val favorites: StateFlow<List<FavoriteCalculation>> = _favorites.asStateFlow()

    // Quiz Session States
    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedOptionIndex = MutableStateFlow<Int?>(null)
    val selectedOptionIndex: StateFlow<Int?> = _selectedOptionIndex.asStateFlow()

    private val _isAnswerSubmitted = MutableStateFlow(false)
    val isAnswerSubmitted: StateFlow<Boolean> = _isAnswerSubmitted.asStateFlow()

    private val _correctAnswersCount = MutableStateFlow(0)
    val correctAnswersCount: StateFlow<Int> = _correctAnswersCount.asStateFlow()

    private val _quizCompleted = MutableStateFlow(false)
    val quizCompleted: StateFlow<Boolean> = _quizCompleted.asStateFlow()

    init {
        loadFavoritesFromPrefs()
    }

    fun navigateTo(screen: ActiveScreen) {
        _activeScreen.value = screen
        // Reset dynamic states depending on the route
        if (screen is ActiveScreen.CalculatorDetail) {
            _inputs.value = emptyMap()
            _calculationResult.value = null
        }
    }

    fun selectTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun updateInput(field: String, value: String) {
        val current = _inputs.value.toMutableMap()
        current[field] = value
        _inputs.value = current
        // Live update calculation result!
        triggerRecalculation()
    }

    fun clearResult() {
        _inputs.value = emptyMap()
        _calculationResult.value = null
    }

    // Interactive Electrical Formulas Algorithms
    private fun triggerRecalculation() {
        val screen = _activeScreen.value
        if (screen !is ActiveScreen.CalculatorDetail) return

        val moduleIndex = screen.moduleIndex
        val calcIndex = screen.calcIndex

        // Module ID mappings:
        // Module 1 (Carga e Demanda) -> Calc 0 (Carga Instalada), Calc 1 (Demanda Máxima), Calc 2 (Carga por m²)
        // Module 2 (Condutores) -> Calc 0 (Seção Mínima), Calc 1 (Queda de Tensão), Calc 2 (Corrente de Projeto)
        // Module 3 (Proteção) -> Calc 0 (Disjuntores), Calc 1 (DR)

        val map = _inputs.value
        try {
            when (moduleIndex) {
                0 -> { // Module 1
                    when (calcIndex) {
                        0 -> calculateCargaInstalada(map)
                        1 -> calculateDemandaMaxima(map)
                        2 -> calculateCargaPorM2(map)
                    }
                }
                1 -> { // Module 2
                    when (calcIndex) {
                        0 -> calculateSecaoMinimaCondutor(map)
                        1 -> calculateQuedaTensao(map)
                        2 -> calculateCorrenteProjeto(map)
                    }
                }
                2 -> { // Module 3
                    when (calcIndex) {
                        0 -> calculateDimensionamentoDisjuntor(map)
                        1 -> calculateDimensionamentoDR(map)
                    }
                }
            }
        } catch (e: Exception) {
            _calculationResult.value = CalculationOutput(
                success = false,
                outputDescription = "Valores incompletos ou inadequados para cálculo. Preencha todos os campos corretamente.",
                stepByStep = "",
                recommendations = "Insira números decimais válidos. Exemplo: use '.' para decimais, não ','"
            )
        }
    }

    private fun calculateCargaInstalada(map: Map<String, String>) {
        val pLuz = map["pLuz"]?.toDoubleOrNull() ?: 0.0
        val pTug = map["pTug"]?.toDoubleOrNull() ?: 0.0
        val pChuveiro = map["pChuveiro"]?.toDoubleOrNull() ?: 0.0
        val pMotor = map["pMotor"]?.toDoubleOrNull() ?: 0.0

        if (pLuz <= 0 && pTug <= 0 && pChuveiro <= 0 && pMotor <= 0) {
            _calculationResult.value = null
            return
        }

        val total = pLuz + pTug + pChuveiro + pMotor
        val description = "Potência Instalada Total (P_inst): %.2f W (ou %.2f kW)".format(total, total / 1000.0)

        val steps = """
            P_inst = Σ(P_i)
            P_inst = P_iluminação + P_tug + P_chuveiro + P_motor
            P_inst = %.2fW + %.2fW + %.2fW + %.2fW
            P_inst = %.2f W
        """.trimIndent().format(pLuz, pTug, pChuveiro, pMotor, total)

        val level = if (total > 15000.0) "Entrada Trifásica sugerida devido à carga > 15 kW" else "Entrada Monofásica/Bifásica aplicável sob limites locais da concessionária."
        val rec = "Com fulcro na NBR 5410 Seção 4.2.0, as cargas devem ser balanceadas de forma equitativa entre as fases da instalação.\n$level"

        _calculationResult.value = CalculationOutput(true, description, steps, rec)
    }

    private fun calculateDemandaMaxima(map: Map<String, String>) {
        val pInst = map["pInst"]?.toDoubleOrNull() ?: 0.0
        val fDemanda = map["fDemanda"]?.toDoubleOrNull() ?: 1.0

        if (pInst <= 0) {
            _calculationResult.value = null
            return
        }

        val pDem = pInst * fDemanda
        val description = "Demanda Máxima Estimada (P_dem): %.2f W (ou %.2f kW)".format(pDem, pDem / 1000.0)

        val steps = """
            P_dem = P_inst × F_d
            P_dem = %.2f W × %.2f
            P_dem = %.2f W
        """.trimIndent().format(pInst, fDemanda, pDem)

        val rec = "Conforme a NBR 5410, o fator de demanda (F_d) prevê racionalidade econômica. O cabo de sustentação de entrada e os barramentos devem ser dimensionados para sustentar P_dem, e nunca P_inst total, reduzindo custos desnecessários em até 40%."

        _calculationResult.value = CalculationOutput(true, description, steps, rec)
    }

    private fun calculateCargaPorM2(map: Map<String, String>) {
        val comp = map["comp"]?.toDoubleOrNull() ?: 0.0
        val larg = map["larg"]?.toDoubleOrNull() ?: 0.0

        if (comp <= 0 || larg <= 0) {
            _calculationResult.value = null
            return
        }

        val area = comp * larg

        // Progressive regulation NBR 5410 (9.5.2.1):
        // Côte <= 6 m² -> 100 VA
        // Côte > 6 m² -> 100 VA for first 6 m² + 60 VA for each complete increment of 4 m²
        val powerVA: Double
        val derivation: String
        if (area <= 6.0) {
            powerVA = 100.0
            derivation = "Área da sala menor ou idêntica a 6,0 m² → Mínimo obrigatório = 100 VA"
        } else {
            val leftover = area - 6.0
            val increments = (leftover / 4.0).toInt()
            powerVA = 100.0 + (increments * 60.0)
            derivation = "Área > 6 m² → 100 VA (primeiros 6 m²) + %d incrementos completos de 4 m² × 60 VA = %.0f VA".format(increments, powerVA)
        }

        val description = "Área: %.2f m²\nCarga Mínima de Iluminação Exigida: %.0f VA".format(area, powerVA)

        val steps = """
            Área Total (A) = Comprimento × Largura
            A = %.2f m × %.2f m = %.2f m²
            
            Regra NBR 5410 (Seção 9.5.2.1):
            $derivation
        """.trimIndent().format(comp, larg, area)

        val rec = "Esta é a potência mínima da iluminação do cômodo. Para tomadas de uso geral (TUG), prever no mínimo 1 ponto para cada 5 m ou fração de perímetro de paredes para quartos/salas, e 1 ponto a cada 3,5m de perímetro em cozinhas/serviços."

        _calculationResult.value = CalculationOutput(true, description, steps, rec)
    }

    private fun calculateSecaoMinimaCondutor(map: Map<String, String>) {
        val iP = map["iP"]?.toDoubleOrNull() ?: 0.0
        val isTug = map["isTug"]?.equals("sim", ignoreCase = true) ?: false

        if (iP <= 0) {
            _calculationResult.value = null
            return
        }

        // Simulating NBR 5410 capacity calculation
        // For copper, common B1 PVC Method capacities of standard cables:
        // 1.5mm² -> 17.5 A
        // 2.5mm² -> 24 A
        // 4mm² -> 32 A
        // 6mm² -> 41 A
        // 10mm² -> 57 A
        // 16mm² -> 76 A

        val rawS: Double
        val normMinimum = if (isTug) 2.5 else 1.5
        val explanation: String

        if (iP <= 17.5) {
            rawS = 1.5
            explanation = "Capacidade de condução térmica de 1.5 mm² (até 17.5A Método B1) atende o circuito."
        } else if (iP <= 24.0) {
            rawS = 2.5
            explanation = "Capacidade de condução térmica de 2.5 mm² (até 24A Método B1) atende o circuito."
        } else if (iP <= 32.0) {
            rawS = 4.0
            explanation = "Capacidade de condução térmica de 4.0 mm² (até 32A Método B1) atende o circuito."
        } else if (iP <= 41.0) {
            rawS = 6.0
            explanation = "Capacidade de condução térmica de 6.0 mm² (até 41A Método B1) atende o circuito."
        } else if (iP <= 57.0) {
            rawS = 10.0
            explanation = "Capacidade de condução térmica de 10.0 mm² (até 57A Método B1) atende o circuito."
        } else if (iP <= 76.0) {
            rawS = 16.0
            explanation = "Capacidade de condução térmica de 16.0 mm² (até 76A Método B1) atende o circuito."
        } else {
            rawS = 25.0
            explanation = "Corrente muito alta pré-indicada. Sugere-se fiação reforçada acima de 25.0 mm² e análise por engenheiro especialista."
        }

        val finalizedS = maxOf(rawS, normMinimum)
        val description = "Seção Recomenda de Cabo Fase (S): %.1f mm²".format(finalizedS)

        val steps = """
            Corrente de Projeto (I_p) = %.2f A
            Circuito para Tomadas/Força? = ${if (isTug) "Sím" else "Não"}
            Bitola Mínima Regulamentar por Norma (Tabela 47) = %.1f mm²
            
            Análise Térmica:
            Conforme tabela 36 da NBR 5410:
            $explanation
            Resultante Final (Maior entre Térmica e Mínimo da Norma): %.1f mm²
        """.trimIndent().format(iP, normMinimum, finalizedS)

        val rec = "Atenção: Este cálculo supõe método padrão de condutores de cobre em eletroduto embutido em alvenaria (Método B1) com máximo de 3 condutores agrupados. Havendo mais agrupamentos de fiação, aplique fatores de redução."

        _calculationResult.value = CalculationOutput(true, description, steps, rec)
    }

    private fun calculateQuedaTensao(map: Map<String, String>) {
        val comprimento = map["comprimento"]?.toDoubleOrNull() ?: 0.0
        val corrente = map["corrente"]?.toDoubleOrNull() ?: 0.0
        val bitola = map["bitola"]?.toDoubleOrNull() ?: 0.0
        val tensao = map["tensao"]?.toDoubleOrNull() ?: 220.0
        val isAluminum = map["material"]?.equals("aluminio", ignoreCase = true) ?: false

        if (comprimento <= 0 || corrente <= 0 || bitola <= 0) {
            _calculationResult.value = null
            return
        }

        val rho = if (isAluminum) 0.0282 else 0.0172
        val matName = if (isAluminum) "Alumínio (ρ=0,0282)" else "Cobre (ρ=0,0172)"

        // Monofásico / Bifásico tradicional multiplier: 2
        val quedaVolts = (2.0 * comprimento * corrente * rho) / bitola
        val quedaPercentual = (quedaVolts / tensao) * 100.0
        val approved = quedaPercentual <= 4.0

        val description = "Queda de Tensão (ΔU): %.2f V\nPorcentagem de Perda (ΔU%%): %.2f %%".format(quedaVolts, quedaPercentual)

        val steps = """
            Fórmula: ΔU = (2 × L × I × ρ) / S
            Fórmula Queda %: ΔU% = (ΔU / U_n) × 100%
            
            Onde:
            - L (Comprimento) = %.1f m
            - I (Corrente) = %.1f A
            - S (Seção do Cabo) = %.1f mm²
            - U_n (Tensão) = %.1f V
            - Material = $matName
            
            Cálculo:
            ΔU = (2 × %.1f × %.1f × $rho) / %.1f
            ΔU = %.3f Volts
            
            ΔU%% = (%.3f / %.1f) × 100% = %.2f %%
        """.trimIndent().format(comprimento, corrente, bitola, tensao, comprimento, corrente, bitola, quedaVolts, quedaVolts, tensao, quedaPercentual)

        val statusSymbol = if (approved) "✅ APROVADO (Dentro do limite de 4,0% da NBR 5410)" else "❌ EXCEDIDO (Sua queda de tensão de %.2f%% ultrapssa o limite máximo de 4,0%%)".format(quedaPercentual)
        val rec = "Status de Conformidade: $statusSymbol\n\nCaso o status esteja EXCEDIDO, rever o dimensionamento para uma bitola de seção maior (S) ou encurtar a trajetória do eletroduto para reduzir a impedância."

        _calculationResult.value = CalculationOutput(true, description, steps, rec)
    }

    private fun calculateCorrenteProjeto(map: Map<String, String>) {
        val potencia = map["potencia"]?.toDoubleOrNull() ?: 0.0
        val tensao = map["tensao"]?.toDoubleOrNull() ?: 220.0
        val fp = map["fp"]?.toDoubleOrNull() ?: 1.0

        if (potencia <= 0 || tensao <= 0) {
            _calculationResult.value = null
            return
        }

        val i = potencia / (tensao * fp)
        val description = "Corrente de Projeto (I_p): %.2f A".format(i)

        val steps = """
            I_p = P / (U × cosφ)
            I_p = %.1f W / (%.1f V × %.2f)
            I_p = %.2f A
        """.trimIndent().format(potencia, tensao, fp, i)

        val rec = "Essa corrente é a base absoluta para a seleção do cabo de fase pelo critério de capacidade térmica e para o dimensionamento do disjuntor do circuito correspondente."

        _calculationResult.value = CalculationOutput(true, description, steps, rec)
    }

    private fun calculateDimensionamentoDisjuntor(map: Map<String, String>) {
        val iP = map["iP"]?.toDoubleOrNull() ?: 0.0
        val iZ = map["iZ"]?.toDoubleOrNull() ?: 0.0

        if (iP <= 0 || iZ <= 0) {
            _calculationResult.value = null
            return
        }

        // Target standard DIN disjunctor currents: 6, 10, 13, 16, 20, 25, 32, 40, 50, 63, 70, 80, 100
        val standardDIN = listOf(6, 10, 13, 16, 20, 25, 32, 40, 50, 63, 70, 80, 100)
        var recommendedIn = -1
        for (din in standardDIN) {
            if (din >= iP && din <= iZ) {
                recommendedIn = din
                break
            }
        }

        val success = recommendedIn != -1
        val description = if (success) {
            "Disjuntor Sugerido (I_n): DIN de $recommendedIn A"
        } else {
            "Sem disjuntor comercial padrão disponível para satisfazer a inequação de coordenação."
        }

        val steps = """
            Norma da inequação protetora de sobrecorrente (NBR 5410 Seção 5.3):
            I_p ≤ I_n ≤ I_z
            
            Onde:
            - Corrente de Projeto (I_p) = %.2f A
            - Capacidade do Condutor (I_z) = %.2f A
            
            DIN comerciais: [6A, 10A, 13A, 16A, 20A, 25A, 32A, 40A, 50A, 63A, 70A, 80A, 100A]
        """.trimIndent().format(iP, iZ)

        val rec = if (success) {
            "✅ O disjuntor de $recommendedIn A atende plenamente ao circuito. Se for circuito com elevadas correntes de partida como motores ou condicionadores de ar, utilize Curva C. Para chuveiros e cargas puramente resistivas, utilize Curva B."
        } else {
            "⚠️ ATENÇÃO EXTREMA: A corrente de projeto (%.2fA) está muito próxima ou excede a capacidade limite do condutor (%.2fA). Vocẽ DEVE aumentar a bitola do cabo de fase para expandir I_z e garantir margem segura de atuação para o disjuntor.".format(iP, iZ)
        }

        _calculationResult.value = CalculationOutput(success, description, steps, rec)
    }

    private fun calculateDimensionamentoDR(map: Map<String, String>) {
        val isAreaMolhada = map["isAreaMolhada"]?.equals("sim", ignoreCase = true) ?: false

        val sensitivity = if (isAreaMolhada) "30 mA (Alta Sensibilidade)" else "30 a 300 mA (Seletivo / Incêndio)"
        val description = "Sensibilidade Recomendada do DR (I_Δn): $sensitivity"

        val steps = """
            Proteção Diferencial Residual (Dispositivo DR) NBR 5410:
            O uso de DR de alta sensibilidade (≤ 30 mA) é compulsório para:
            - Circuitos que sirvam a pontos de tomada em banheiros, cozinhas, lavanderias e áreas externas.
            
            Área molhada/decorada indicada pelo usuário? = ${if (isAreaMolhada) "SIM" else "NÃO"}
        """.trimIndent()

        val rec = "O DR atua desligando a fiação ao detectar fugas de minúsculas correntes que passariam pelo corpo de um indivíduo em caso de choque elétrico acidental. Teste mensalmente o botão 'TESTE' físico do DR no quadro de distribuição."

        _calculationResult.value = CalculationOutput(true, description, steps, rec)
    }

    // SharedPreferences Favorites Logic
    private fun loadFavoritesFromPrefs() {
        val allKeys = prefs.all
        val list = mutableListOf<FavoriteCalculation>()
        for ((key, value) in allKeys) {
            if (key.startsWith("fav_")) {
                val csv = value as? String ?: continue
                val parts = csv.split("|")
                if (parts.size >= 4) {
                    list.add(
                        FavoriteCalculation(
                            id = key,
                            title = parts[0],
                            moduleName = parts[1],
                            resultText = parts[2],
                            timestamp = parts[3].toLongOrNull() ?: 0L
                        )
                    )
                }
            }
        }
        _favorites.value = list.sortedByDescending { it.timestamp }
    }

    fun saveAsFavorite(title: String, moduleName: String, resultText: String) {
        val id = "fav_${System.currentTimeMillis()}"
        val csv = "$title|$moduleName|$resultText|${System.currentTimeMillis()}"
        prefs.edit().putString(id, csv).apply()
        loadFavoritesFromPrefs()
    }

    fun deleteFavorite(id: String) {
        prefs.edit().remove(id).apply()
        loadFavoritesFromPrefs()
    }

    // Quiz Session Mechanical Logics
    fun startNewQuiz() {
        _currentQuestionIndex.value = 0
        _selectedOptionIndex.value = null
        _isAnswerSubmitted.value = false
        _correctAnswersCount.value = 0
        _quizCompleted.value = false
        _activeScreen.value = ActiveScreen.QuizSession
    }

    fun selectOption(index: Int) {
        if (_isAnswerSubmitted.value) return
        _selectedOptionIndex.value = index
    }

    fun submitAnswer() {
        val sel = _selectedOptionIndex.value ?: return
        if (_isAnswerSubmitted.value) return

        _isAnswerSubmitted.value = true
        val currentQuestion = ElectricalRepository.quizQuestions[_currentQuestionIndex.value]

        if (sel == currentQuestion.correctIndex) {
            _correctAnswersCount.value = _correctAnswersCount.value + 1
        }
    }

    fun nextQuestion() {
        _selectedOptionIndex.value = null
        _isAnswerSubmitted.value = false

        val nextIndex = _currentQuestionIndex.value + 1
        if (nextIndex < ElectricalRepository.quizQuestions.size) {
            _currentQuestionIndex.value = nextIndex
        } else {
            _quizCompleted.value = true
        }
    }

    fun exitQuiz() {
        _activeScreen.value = ActiveScreen.Dashboard
    }
}

data class CalculationOutput(
    val success: Boolean,
    val outputDescription: String,
    val stepByStep: String,
    val recommendations: String
)

data class FavoriteCalculation(
    val id: String,
    val title: String,
    val moduleName: String,
    val resultText: String,
    val timestamp: Long
)
