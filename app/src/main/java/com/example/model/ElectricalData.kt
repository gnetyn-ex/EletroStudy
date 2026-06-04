package com.example.model

data class ReferenceTable(
    val title: String,
    val description: String,
    val rows: List<Pair<String, String>>
)

data class ConstantItem(
    val name: String,
    val value: String,
    val description: String
)

data class Calculation(
    val nome: String,
    val formula: String,
    val variaveis: Map<String, String>,
    val exemplo: String,
    val unidade: String,
    val aplicacao: String,
    val normaReferencia: String
)

data class StudyModule(
    val id: Int,
    val nome: String,
    val descricao: String,
    val calculos: List<Calculation>
)

data class QuizQuestion(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

object ElectricalRepository {
    val normList = listOf(
        "NBR 5410" to "Instalações elétricas de baixa tensão (até 1000V CA). É a norma pilar de qualquer eletricista residencial e predial.",
        "NBR 5419" to "Sistemas de Proteção contra Descargas Atmosféricas (SPDA). Rege para-raios e aterramentos de edificações.",
        "NBR 13570" to "Instalações elétricas em locais de afluência de público (normas de segurança rígidas contra pânico).",
        "NBR ISO/CIE 8995" to "Iluminação de ambientes de trabalho. Define as iluminâncias e índices de ofuscamento mínimos por tarefa.",
        "NBR 14039" to "Instalações elétricas de média tensão (de 1,0 kV a 36,2 kV), fundamental para condomínios e indústrias grandes."
    )

    val constantes = listOf(
        ConstantItem("Resistividade do Cobre (ρ_cu)", "0,0172 Ω·mm²/m", "Resistividade padrão a 20°C para condutores de cobre."),
        ConstantItem("Resistividade do Alumínio (ρ_al)", "0,0282 Ω·mm²/m", "Resistividade padrão a 20°C para condutores de alumínio."),
        ConstantItem("Frequência de Rede", "60 Hz", "Frequência padrão do sistema elétrico nacional brasileiro."),
        ConstantItem("Limite de Queda de Tensão", "4,0%", "Queda máxima permitida da origem do circuito até o ponto terminal."),
        ConstantItem("Resistência Máxima SPDA", "10 Ω", "Recomendação máxima de aterramento protetivo contra surtos atmosféricos."),
        ConstantItem("Resistência Equipamentos", "100 Ω", "Valor padrão recomendado para aterramento funcional de carcaças.")
    )

    val tabelasReferencia = listOf(
        ReferenceTable(
            title = "NBR 5410 - Tabela 47: Seções Mínimas de Condutores",
            description = "Seção transversal mínima dos condutores de fase em cobre para cada tipo de circuito.",
            rows = listOf(
                "Circuitos de Iluminação" to "1,5 mm²",
                "Circuitos de Força (Tomadas TUG/TUE)" to "2,5 mm²",
                "Circuitos de Sinalização / Controle" to "0,5 mm²"
            )
        ),
        ReferenceTable(
            title = "NBR 5410 - Tabela 58: Seção do Fio Terra (PE)",
            description = "Dimensões recomendadas do fio de proteção com base na seção da fase.",
            rows = listOf(
                "Fase ≤ 16 mm²" to "Mesma seção da Fase (S_PE = S_fase)",
                "Fase entre 16 e 35 mm²" to "Mínimo 16 mm² (S_PE = 16)",
                "Fase > 35 mm²" to "Metade da seção da Fase (S_PE = S_fase / 2)"
            )
        ),
        ReferenceTable(
            title = "NBR 5410 - Taxa de Preenchimento de Eletrodutos",
            description = "Limite máximo de ocupação interna útil de eletrodutos por cabos eletrônicos/elétricos.",
            rows = listOf(
                "1 condutor" to "Máximo 53% da área interna",
                "2 condutores" to "Máximo 31% da área interna",
                "3 ou mais condutores" to "Máximo 40% da área interna (Recomendado)"
            )
        )
    )

    val modules = listOf(
        StudyModule(
            id = 1,
            nome = "Cálculos de Carga e Demanda",
            descricao = "Determinação das cargas elétricas totais e demanda máxima de instalações",
            calculos = listOf(
                Calculation(
                    "Cálculo de Carga Instalada",
                    "P_inst = Σ(P_i)",
                    mapOf("P_inst" to "Potência instalada total (W ou kW)", "P_i" to "Potência nominal de cada ponto"),
                    "P_inst = 1500W (luz) + 4400W (TUG) + 5500W (chuveiro) = 11.400W",
                    "Watts (W) ou kW",
                    "Dimensionamento de disjuntores de entrada e padrões de entrada da concessionária.",
                    "NBR 5410 - Seção 4.2"
                ),
                Calculation(
                    "Cálculo de Demanda Máxima",
                    "P_dem = P_inst × F_d",
                    mapOf("P_dem" to "Demanda estimada (W)", "P_inst" to "Potência instalada", "F_d" to "Fator de Demanda"),
                    "P_dem = 11.400W × 0,65 = 7.410W",
                    "Watts (W) ou kW",
                    "Dimensionamento do transformador de distribuição interna urbano, cabeamento de entrada e seletividade.",
                    "NBR 5410 - Fatores de Demanda"
                ),
                Calculation(
                    "Cálculo de Carga por m²",
                    "P_min = A × P_m²",
                    mapOf("P_min" to "Potência mínima exigida (VA)", "A" to "Área do cômodo (m²)", "P_m²" to "Potência por metro quadrado"),
                    "P_min = 25m² × 100VA/m² = 2.500VA",
                    "Volt-Ampere (VA)",
                    "Determinação regulamentar inicial para quantidade e capacidade de iluminação.",
                    "NBR 5410 - Tabela 1"
                )
            )
        ),
        StudyModule(
            id = 2,
            nome = "Cálculos de Condutores",
            descricao = "Dimensionamento de cabos e fios para condução segura de corrente elétrica",
            calculos = listOf(
                Calculation(
                    "Seção Mínima do Condutor",
                    "S = I_p / (k × f_t × f_agrup)",
                    mapOf("S" to "Seção (mm²)", "I_p" to "Corrente de projeto (A)", "f_t" to "Fator temperatura", "f_agrup" to "Fator agrupamento"),
                    "Corrente terminal 32A com fator de correção indica no mínimo cabo de 6,0 mm².",
                    "mm²",
                    "Seleção mecânica e térmica prudente dos condutores elétricos residenciais ou prediais.",
                    "NBR 5410 - Tabelas 36 a 39"
                ),
                Calculation(
                    "Queda de Tensão Terminal",
                    "ΔU% = (ΔU / U_n) × 100%",
                    mapOf("ΔU%" to "Queda percentual", "ΔU" to "Queda absoluta (V)", "U_n" to "Tension nominal (V)"),
                    "ΔU% = (8V / 127V) × 100 = 6,3% (Excede o limite máximo legal de 4%)",
                    "Percentual (%)",
                    "Evitar funcionamento defeituoso de motores e queimadura de lâmpadas decorrentes de fiação comprida.",
                    "NBR 5410 - Seção 6.2.7"
                ),
                Calculation(
                    "Corrente de Projeto",
                    "I_p = P / (U × cosφ)",
                    mapOf("I_p" to "Corrente de projeto (A)", "P" to "Potência ativa ativa (W)", "U" to "Tensão (V)", "cosφ" to "Fator de potência"),
                    "I_p = 5500W / (220V × 1,0) = 25,0 A (chuveiro elétrico típico)",
                    "Amperes (A)",
                    "Determinar a corrente real de trabalho contínuo de cargas de iluminação, motores e resistores.",
                    "NBR 5410 - Seção 6.2.2"
                )
            )
        ),
        StudyModule(
            id = 3,
            nome = "Cálculos de Proteção",
            descricao = "Dimensionamento de dispositivos de proteção contra sobrecorrente e falhas",
            calculos = listOf(
                Calculation(
                    "Dimensionamento de Disjuntores",
                    "I_n ≥ I_p  e  I_n ≤ I_z",
                    mapOf("I_n" to "Corrente do disjuntor (A)", "I_p" to "Corrente de projeto", "I_z" to "Limite térmico do cabo"),
                    "Se I_p = 25A e I_z = 36A, escolhemos disjuntor de 32A.",
                    "Amperes (A)",
                    "Proteger condutores contra fogo derivado de sobrecarga termoelétrica permanente.",
                    "NBR 5410 - Seção 5.3"
                ),
                Calculation(
                    "Dimensionamento de DR",
                    "I_Δn ≤ 30 mA para proteção pessoal de uso geral",
                    mapOf("I_Δn" to "Corrente nominal de fuga/atuação"),
                    "DR Geral de 30mA desarmando preventivamente com falhas elétricas sutis.",
                    "mA",
                    "Dispositivo essencial para salvar vidas humanas contra o perigo letal de choques induzidos.",
                    "NBR 5410 - Seção 5.1.2"
                )
            )
        )
    )

    val quizQuestions = listOf(
        QuizQuestion(
            1,
            "Qual a queda de tensão máxima permitida em circuitos terminais residenciais segundo a NBR 5410, medida da origem até o ponto?",
            listOf("2%", "3%", "4%", "5%"),
            2,
            "De acordo com a NBR 5410 Seção 6.2.7, a queda de tensão terminal máxima aceitável para circuitos alimentados da rede de BT é de 4,0%."
        ),
        QuizQuestion(
            2,
            "Qual a seção mínima regulamentar para condutores de cobre em circuitos de iluminação residencial constante na NBR 5410?",
            listOf("0,75 mm²", "1,0 mm²", "1,5 mm²", "2,5 mm²"),
            2,
            "A Tabela 47 da NBR 5410 prescreve a seção mínima de 1,5 mm² para iluminação com condutores de cobre."
        ),
        QuizQuestion(
            3,
            "Qual o valor correspondente à seção mínima regulamentar para cabos de cobre em tomadas residenciais de uso geral (TUG)?",
            listOf("1,5 mm²", "2,5 mm²", "4,0 mm²", "6,0 mm²"),
            1,
            "A fiação para circuitos de força e tomadas residenciais de uso geral deve possuir seção mínima de 2,5 mm² segundo a NBR 5410."
        ),
        QuizQuestion(
            4,
            "Qual deve ser o limite máximo de sensibilidade à corrente diferencial (I_Δn) de um dispositivo DR voltado para a salvaguarda humana contra choques acidentais?",
            listOf("10 mA", "30 mA", "100 mA", "300 mA"),
            1,
            "A norma prescreve DR de alta sensibilidade com corrente nominal de atuação menor ou igual a 30 mA para mitigar acidentes fatais por fibrilação cardíaca."
        ),
        QuizQuestion(
            5,
            "Qual é a taxa máxima de preenchimento admissível por norma em um eletroduto que passará 3 ou mais condutores elétricos?",
            listOf("31%", "40%", "50%", "60%"),
            1,
            "A taxa máxima de ocupação regulamentar no eletroduto com 3 ou mais condutores ativos é fixada em no máximo 40% (Seção 6.2.11 da NBR 5410)."
        ),
        QuizQuestion(
            6,
            "Se o circuito elétrico possui fases com bitola de 10 mm², qual deve ser a bitola regulamentar correspondente ao condutor terra (PE)?",
            listOf("2,5 mm²", "4,0 mm²", "6,0 mm²", "10 mm²"),
            3,
            "Pela Tabela 58 da NBR 5410, para fases com bitola inferior ou igual a 16 mm², a fiação terra PE tem que ser de igual diâmetro (10 mm²)."
        ),
        QuizQuestion(
            7,
            "Para uma fiação de fase de 25 mm², a tabela 58 define a bitola do condutor terra PE como:",
            listOf("10 mm²", "16 mm²", "25 mm²", "35 mm²"),
            1,
            "Para fases entre 16 mm² e 35 mm², a bitola mínima do terra protetivo PE é padronizada em 16 mm²."
        ),
        QuizQuestion(
            8,
            "Qual é o objetivo principal do Fator de Simultaneidade (Fs) no dimensionamento de quadros de distribuição?",
            listOf("Simular tensões de curto-circuito", "Limitar as harmônicas da rede", "Estimar racionalmente a porcentagem de aparelhos ligados ao mesmo tempo", "Corrigir a resistividade sob altas temperaturas"),
            2,
            "O fator de simultaneidade avalia a probabilidade realista de que nem todos os aparelhos de elevada carga funcionarão simultaneamente, economizando recursos."
        )
    )
}
