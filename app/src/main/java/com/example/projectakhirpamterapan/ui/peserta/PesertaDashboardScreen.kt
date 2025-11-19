package com.example.projectakhirpamterapan.ui.peserta

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.projectakhirpamterapan.ui.peserta.components.JoinEventDialog
import com.example.projectakhirpamterapan.ui.peserta.components.PesertaEventCard
import com.example.projectakhirpamterapan.ui.peserta.components.PesertaHeroSection
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PesertaDashboardScreen(
    vm: PesertaDashboardViewModel,
    userName: String,
    onBackToRole: () -> Unit,
    onOpenEventDetail: (
        eventId: Int,
        eventTitle: String,
        eventDate: String?,
        eventTime: String?,
        eventLocation: String,
        eventStatus: String
    ) -> Unit
) {
    val uiState by vm.uiState.collectAsState()

    var showJoinDialog by remember { mutableStateOf(false) }
    var snackMessage by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // ZXing Embedded
    val scanQrLauncher = rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result ->
        if (result.contents != null) {
            val contents = result.contents
            vm.joinByQrCode(contents.trim()) { success, message ->
                snackMessage = message.ifBlank {
                    if (success) "Berhasil bergabung ke event." else "Gagal bergabung ke event."
                }
            }
        } else {
            snackMessage = "Scan dibatalkan."
        }
    }

    // Permission kamera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startEmbeddedQrScan(scanQrLauncher)
        } else {
            snackMessage = "Izin kamera diperlukan untuk scan QR."
        }
    }

    LaunchedEffect(snackMessage) {
        snackMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackMessage = null
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dashboard Peserta") },
                navigationIcon = {
                    IconButton(onClick = onBackToRole) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val hasCameraPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasCameraPermission) {
                        startEmbeddedQrScan(scanQrLauncher)
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.QrCodeScanner,
                    contentDescription = "Scan QR untuk gabung event"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                    start = 16.dp,
                    end = 16.dp
                )
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.events.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Maaf, event yang kamu ikuti belum tersedia.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Tap tombol QR di kanan bawah untuk gabung event dengan scan QR Invitation.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(12.dp))
                        TextButton(onClick = { showJoinDialog = true }) {
                            Text("Atau masukkan kode secara manual")
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        item {
                            PesertaHeroSection(
                                userName = userName,
                                totalEvents = uiState.events.size
                            )
                            Spacer(Modifier.height(12.dp))
                        }

                        items(uiState.events, key = { it.id }) { event ->
                            PesertaEventCard(
                                event = event,
                                onManageClick = {
                                    onOpenEventDetail(
                                        event.id,
                                        event.title,
                                        event.eventDateRaw,
                                        event.eventTimeRaw,
                                        event.location,
                                        event.status
                                    )
                                }
                            )
                        }

                        item {
                            Spacer(Modifier.height(8.dp))
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                TextButton(onClick = { showJoinDialog = true }) {
                                    Text("Masukkan kode QR secara manual")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showJoinDialog) {
        JoinEventDialog(
            onJoin = { code ->
                vm.joinByQrCode(code.trim()) { _, message ->
                    snackMessage = message
                }
                showJoinDialog = false
            },
            onDismiss = { showJoinDialog = false }
        )
    }
}

private fun startEmbeddedQrScan(
    launcher: androidx.activity.result.ActivityResultLauncher<ScanOptions>
) {
    val options = ScanOptions().apply {
        setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        setPrompt("Arahkan kamera ke QR Invitation")
        setBeepEnabled(true)
        setCameraId(0)
        setOrientationLocked(false)
    }
    launcher.launch(options)
}
