package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Calculation
import com.example.model.ElectricalRepository
import com.example.viewmodel.ActiveScreen
import com.example.viewmodel.ElectricianViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: ElectricianViewModel) {
    val activeScreen by viewModel.activeScreen.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing // Prevent camera notch and navbar overlaps
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val screen = activeScreen) {
                is ActiveScreen.Dashboard -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Header Bar
                        DashboardHeader()

                        // Tabs Select Bar
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.primary,
                            divider = { HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant) }
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { viewModel.selectTab(0) },
                                modifier = Modifier.testTag("tab_calculators"),
                                text = { Text("Cálculos", fontWeight = FontWeight.Bold) },
                                icon = { Icon(Icons.Default.Calculate, contentDescription = "Calculadoras") }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { viewModel.selectTab(1) },
                                modifier = Modifier.testTag("tab_library"),
                                text = { Text("Biblioteca", fontWeight = FontWeight.Bold) },
                                icon = { Icon(Icons.Default.Book, contentDescription = "Normas NBR") }
                            )
                            Tab(
                                selected = selectedTab == 2,
                                onClick = { viewModel.selectTab(2) },
                                modifier = Modifier.testTag("tab_simulado"),
                                text = { Text("Simulado", fontWeight = FontWeight.Bold) },
                                icon = { Icon(Icons.Default.School, contentDescription = "Simulado NBR") }
                            )
                        }

                        // Responsive content selection
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            when (selectedTab) {
                                0 -> CalculatorsDeckTab(viewModel)
                                1 -> LibraryStudyTab()
                                2 -> QuizHomeTab(viewModel)
                            }
                        }
                    }
                }
                is ActiveScreen.CalculatorDetail -> {
                    CalculatorDetailScreen(
                        calcIndex = screen.calcIndex,
                        moduleIndex = screen.moduleIndex,
                        viewModel = viewModel
                    )
                }
                is ActiveScreen.QuizSession -> {
                    QuizPlayScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun DashboardHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        Color.Transparent
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stylized Bolt Indicator
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ElectricBolt,
                    contentDescription = "Eletricista Logo",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = "Eletricista Pro",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "NBR 5410 • Cálculos de Campo & Aprendizado",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }
}


@Composable
fun CalculatorsDeckTab(viewModel: ElectricianViewModel) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            // Welcome Card explaining use
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = com.example.R.drawable.eletricista_logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Guia de Cálculos Rápidos",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Selecione uma calculadora abaixo para dimensionar circuitos, condutores ou disjuntores com base na NBR 5410.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                        )
                    }
                }
            }
        }

        // Loop through standard modules and calculations from ElectricalRepository
        items(ElectricalRepository.modules.size) { moduleIndex ->
            val module = ElectricalRepository.modules[moduleIndex]

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Bolt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = module.nome,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = module.descricao,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(start = 28.dp, bottom = 4.dp)
                )

                // Render calculations in module
                module.calculos.forEachIndexed { calcIndex, calc ->
                    Card(
                        onClick = {
                            viewModel.navigateTo(ActiveScreen.CalculatorDetail(calcIndex, moduleIndex))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .testTag("calc_card_${moduleIndex}_$calcIndex"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = calc.nome,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Fórmula: ${calc.formula}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Norma: ${calc.normaReferencia}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Abrir Calculadora",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        // Favorites Sub-Selection Display
        if (favorites.isNotEmpty()) {
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Histórico de Favoritos",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            items(favorites) { fav ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = fav.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = fav.moduleName,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            }
                            IconButton(onClick = { viewModel.deleteFavorite(fav.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remover", tint = Color.Red.copy(alpha = 0.7f))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = fav.resultText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun CalculatorDetailScreen(
    calcIndex: Int,
    moduleIndex: Int,
    viewModel: ElectricianViewModel
) {
    val inputs by viewModel.inputs.collectAsStateWithLifecycle()
    val calculationResult by viewModel.calculationResult.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val module = ElectricalRepository.modules[moduleIndex]
    val calc = module.calculos[calcIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Navigation header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(ActiveScreen.Dashboard) },
                modifier = Modifier.testTag("btn_back")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
            }
            Column {
                Text(
                    text = calc.nome,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = module.nome,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // General calculation description card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Fórmula Geral: ${calc.formula}",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = calc.aplicacao,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Unidade do resultado: ${calc.unidade} • Regulado pela: ${calc.normaReferencia}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Input fields specifically constructed for each calculator index
            Text(
                text = "Parâmetros de Entrada",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Dynamic Inputs Renderer
            when (moduleIndex) {
                0 -> { // Module 1
                    when (calcIndex) {
                        0 -> { // Carga Instalada
                            InputField(label = "Potência de Iluminação (Watts)", field = "pLuz", value = inputs["pLuz"] ?: "", placeholder = "Ex: 1500", onValueChange = { viewModel.updateInput("pLuz", it) })
                            InputField(label = "Potência de Tomadas Gerais (Watts)", field = "pTug", value = inputs["pTug"] ?: "", placeholder = "Ex: 4400", onValueChange = { viewModel.updateInput("pTug", it) })
                            InputField(label = "Potência de Chuveiros (Watts)", field = "pChuveiro", value = inputs["pChuveiro"] ?: "", placeholder = "Ex: 5500", onValueChange = { viewModel.updateInput("pChuveiro", it) })
                            InputField(label = "Potência de Outros Motores (Watts)", field = "pMotor", value = inputs["pMotor"] ?: "", placeholder = "Ex: 3000", onValueChange = { viewModel.updateInput("pMotor", it) })
                        }
                        1 -> { // Demanda Máxima
                            InputField(label = "Potência Instalada Total (W)", field = "pInst", value = inputs["pInst"] ?: "", placeholder = "Ex: 14400", onValueChange = { viewModel.updateInput("pInst", it) })
                            InputField(label = "Fator de Demanda (Tabela concessionária - 0 a 1)", field = "fDemanda", value = inputs["fDemanda"] ?: "", placeholder = "Ex: 0.65", onValueChange = { viewModel.updateInput("fDemanda", it) })
                        }
                        2 -> { // Carga por m2
                            InputField(label = "Comprimento do cômodo (Metros)", field = "comp", value = inputs["comp"] ?: "", placeholder = "Ex: 5.0", onValueChange = { viewModel.updateInput("comp", it) })
                            InputField(label = "Largura do cômodo (Metros)", field = "larg", value = inputs["larg"] ?: "", placeholder = "Ex: 3.0", onValueChange = { viewModel.updateInput("larg", it) })
                        }
                    }
                }
                1 -> { // Module 2
                    when (calcIndex) {
                        0 -> { // Secao Minima do Condutor
                            InputField(label = "Corrente de Projeto (Amperes)", field = "iP", value = inputs["iP"] ?: "", placeholder = "Ex: 25.0", onValueChange = { viewModel.updateInput("iP", it) })
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("O circuito alimenta Tomadas de Força?", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                val current = inputs["isTug"] ?: "nao"
                                Switch(
                                    checked = current.equals("sim", true),
                                    onCheckedChange = { viewModel.updateInput("isTug", if (it) "sim" else "nao") },
                                    modifier = Modifier.testTag("switch_tug")
                                )
                            }
                        }
                        1 -> { // Queda de Tensao
                            InputField(label = "Comprimento do Circuito (Metros)", field = "comprimento", value = inputs["comprimento"] ?: "", placeholder = "Ex: 30", onValueChange = { viewModel.updateInput("comprimento", it) })
                            InputField(label = "Corrente de Trabalho do Circuito (Amperes)", field = "corrente", value = inputs["corrente"] ?: "", placeholder = "Ex: 20", onValueChange = { viewModel.updateInput("corrente", it) })
                            InputField(label = "Seção Transversal do Cabo Fase (mm²)", field = "bitola", value = inputs["bitola"] ?: "", placeholder = "Ex: 2.5", onValueChange = { viewModel.updateInput("bitola", it) })
                            InputField(label = "Tensão Nominal do Circuito (Volts)", field = "tensao", value = inputs["tensao"] ?: "220", placeholder = "Ex: 220", onValueChange = { viewModel.updateInput("tensao", it) })

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Condutor de Alumínio? (Cobre por padrão)", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                val isAl = inputs["material"]?.equals("aluminio", true) ?: false
                                Switch(
                                    checked = isAl,
                                    onCheckedChange = { viewModel.updateInput("material", if (it) "aluminio" else "cobre") },
                                    modifier = Modifier.testTag("switch_material")
                                )
                            }
                        }
                        2 -> { // Corrente de Projeto
                            InputField(label = "Potência Ativa Total (Watts)", field = "potencia", value = inputs["potencia"] ?: "", placeholder = "Ex: 5500", onValueChange = { viewModel.updateInput("potencia", it) })
                            InputField(label = "Tensão de Alimentação (Volts)", field = "tensao", value = inputs["tensao"] ?: "220", placeholder = "Ex: 220", onValueChange = { viewModel.updateInput("tensao", it) })
                            InputField(label = "Fator de Potência (cosφ - 0.1 a 1.0)", field = "fp", value = inputs["fp"] ?: "1.0", placeholder = "Ex: 0.95", onValueChange = { viewModel.updateInput("fp", it) })
                        }
                    }
                }
                2 -> { // Module 3
                    when (calcIndex) {
                        0 -> { // Disjuntores
                            InputField(label = "Corrente de Projeto do Circuito iP (A)", field = "iP", value = inputs["iP"] ?: "", placeholder = "Ex: 25.0", onValueChange = { viewModel.updateInput("iP", it) })
                            InputField(label = "Capacidade de Condução Térmica do Cabo iZ (A)", field = "iZ", value = inputs["iZ"] ?: "", placeholder = "Ex: 36.0", onValueChange = { viewModel.updateInput("iZ", it) })
                        }
                        1 -> { // DR
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("O circuito serve Área Úmida/Externa?", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                val molhada = inputs["isAreaMolhada"] ?: "nao"
                                Switch(
                                    checked = molhada.equals("sim", true),
                                    onCheckedChange = { viewModel.updateInput("isAreaMolhada", if (it) "sim" else "nao") },
                                    modifier = Modifier.testTag("switch_area_molhada")
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Output / Result Area (Live or explicit feedback)
            val res = calculationResult
            if (res != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("result_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (res.success) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Red.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, if (res.success) MaterialTheme.colorScheme.primary else Color.Red.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Resultado do Dimensionamento",
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (res.success) MaterialTheme.colorScheme.primary else Color.Red
                            )

                            if (res.success) {
                                Button(
                                    onClick = {
                                        viewModel.saveAsFavorite(calc.nome, module.nome, res.outputDescription)
                                        ScaffoldMessengerHelper.showToast(context, "Salvo nos Favoritos!")
                                    },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.height(32.dp).testTag("btn_save_fav")
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Salvar", fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = res.outputDescription,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        if (res.stepByStep.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(text = "MEMÓRIA DE CÁLCULO:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(4.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = res.stepByStep,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        if (res.recommendations.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(text = "REQUISITOS & RECOMENDAÇÕES (NBR 5410):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = res.recommendations,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.82f),
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            } else {
                // Empty state prompting typing
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Aguardando Parâmetros",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Preencha os campos de texto acima para calcular em tempo real.",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun InputField(
    label: String,
    field: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 14.sp) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_$field"),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}


@Composable
fun LibraryStudyTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Centro de Estudos de Normas Brasileiras",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Referencial teórico profissional consolidado para estudo teórico e verificação elétrica predial e residencial.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Regulatory bodies
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Lista de Normas Relevantes",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ElectricalRepository.normList.forEach { (code, info) ->
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = code,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 14.sp,
                                modifier = Modifier.width(100.dp)
                            )
                            Text(
                                text = info,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // Static Constants
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Constantes Físicas Úteis",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ElectricalRepository.constantes.forEach { const ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = const.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = const.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                            Text(
                                text = const.value,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Dynamic Tables lists
        items(ElectricalRepository.tabelasReferencia) { table ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = table.title,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = table.description,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    table.rows.forEach { (key, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = key, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun QuizHomeTab(viewModel: ElectricianViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Simulado Regulamentar NBR 5410",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Prepare-se para exames de qualificação técnica ou aprimore sua engenharia. São 8 questões interativas cobrindo bitolas mínimas, seções do fio terra PE, queda de tensão máxima admissível, e proteção DR compulsória.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
            textAlign = TextAlign.Center,
            lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = { viewModel.startNewQuiz() },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(48.dp)
                .testTag("btn_start_quiz"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Iniciar Teste Interativo", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}


@Composable
fun QuizPlayScreen(viewModel: ElectricianViewModel) {
    val qIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val selectedOpt by viewModel.selectedOptionIndex.collectAsStateWithLifecycle()
    val isSubmitted by viewModel.isAnswerSubmitted.collectAsStateWithLifecycle()
    val correctCount by viewModel.correctAnswersCount.collectAsStateWithLifecycle()
    val isCompleted by viewModel.quizCompleted.collectAsStateWithLifecycle()

    val totalQuestions = ElectricalRepository.quizQuestions.size

    if (isCompleted) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Simulado Concluído!",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Score breakdown
            Text(
                text = "Acertos: $correctCount de $totalQuestions questões (%.0f%%)".format((correctCount.toDouble() / totalQuestions) * 100.0),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = if (correctCount >= 5) MaterialTheme.colorScheme.primary else Color.Red
            )

            Spacer(modifier = Modifier.height(12.dp))

            val feedbackText = if (correctCount == totalQuestions) {
                "Excelente! Domínio absoluto das bitolas e regras da norma NBR 5410. Você está apto aos cenários profissionais mais complexos!"
            } else if (correctCount >= 5) {
                "Bom desempenho! Você possui bom conhecimento operacional técnico. Revise os pontos onde errou na Biblioteca de Normas para alcançar a perfeição."
            } else {
                "É necessário estudar mais! Releia as seções essenciais de bitola e queda técnica na fiação para assegurar as diretrizes reguladoras."
            }

            Text(
                text = feedbackText,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.startNewQuiz() },
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(48.dp)
                    .testTag("btn_retry_quiz"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Repetir Teste", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { viewModel.exitQuiz() },
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(48.dp)
                    .testTag("btn_exit_quiz"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Voltar ao Painel")
            }
        }
        return
    }

    val question = ElectricalRepository.quizQuestions[qIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Upper progress bar and close button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Questão ${qIndex + 1} de $totalQuestions",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = { viewModel.exitQuiz() }) {
                Icon(Icons.Default.Close, contentDescription = "Sair")
            }
        }

        LinearProgressIndicator(
            progress = { (qIndex + 1).toFloat() / totalQuestions.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Question Statement
        Text(
            text = question.text,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 23.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Choices list
        question.options.forEachIndexed { optIndex, text ->
            val isSelected = selectedOpt == optIndex
            val optionBg = when {
                isSubmitted && optIndex == question.correctIndex -> Color.Green.copy(alpha = 0.12f)
                isSubmitted && isSelected && selectedOpt != question.correctIndex -> Color.Red.copy(alpha = 0.12f)
                isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                else -> MaterialTheme.colorScheme.surface
            }

            val optionBorderColor = when {
                isSubmitted && optIndex == question.correctIndex -> Color.Green
                isSubmitted && isSelected && selectedOpt != question.correctIndex -> Color.Red
                isSelected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.surfaceVariant
            }

            Card(
                onClick = { viewModel.selectOption(optIndex) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("quiz_option_$optIndex"),
                colors = CardDefaults.cardColors(containerColor = optionBg),
                border = BorderStroke(1.5.dp, optionBorderColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ('A'.code + optIndex).toChar().toString(),
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }

                    Text(
                        text = text,
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (isSubmitted) {
                        if (optIndex == question.correctIndex) {
                            Icon(Icons.Default.Check, contentDescription = "Correto", tint = Color.Green)
                        } else if (isSelected) {
                            Icon(Icons.Default.Close, contentDescription = "Incorreto", tint = Color.Red)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Review Explanatory area once answered
        AnimatedVisibility(
            visible = isSubmitted,
            enter = fadeIn() + expandVertically()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(text = "Explicação Regulamentar", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = question.explanation,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Action panel triggered bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            val showSubmit = !isSubmitted
            val isEnabled = selectedOpt != null

            if (showSubmit) {
                Button(
                    onClick = { viewModel.submitAnswer() },
                    enabled = isEnabled,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(48.dp)
                        .testTag("btn_submit_answer"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Confirmar Resposta", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            } else {
                Button(
                    onClick = { viewModel.nextQuestion() },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(48.dp)
                        .testTag("btn_next_question"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (qIndex + 1 == totalQuestions) "Ver Resultado final" else "Próxima Questão",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// Global Singleton Toast Helper avoiding Activity-binding dependencies crash
object ScaffoldMessengerHelper {
    fun showToast(context: Context, text: String) {
        android.widget.Toast.makeText(context, text, android.widget.Toast.LENGTH_SHORT).show()
    }
}
