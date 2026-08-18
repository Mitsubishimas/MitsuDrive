package com.mitsudrive.app.routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Text
import com.mitsudrive.core.location.LocationManager
import com.mitsudrive.core.ui.theme.TextPrimary
import com.mitsudrive.features.chat.impl.repository.ChatRepositoryImpl
import com.mitsudrive.features.chat.ui.screen.ChatListScreen
import com.mitsudrive.features.chat.ui.screen.ChatScreen
import com.mitsudrive.features.chat.ui.viewmodel.ChatListViewModel
import com.mitsudrive.features.chat.ui.viewmodel.ChatViewModel
import com.mitsudrive.features.feed.impl.repository.FeedRepositoryImpl
import com.mitsudrive.features.feed.ui.screen.FeedScreen
import com.mitsudrive.features.feed.ui.viewmodel.FeedViewModel
import com.mitsudrive.features.garage.impl.repository.GarageRepositoryImpl
import com.mitsudrive.features.garage.ui.screen.GarageScreen
import com.mitsudrive.features.garage.ui.viewmodel.GarageViewModel
import com.mitsudrive.features.map.impl.repository.MapRepositoryImpl
import com.mitsudrive.features.map.ui.screen.MapScreen
import com.mitsudrive.features.map.ui.viewmodel.MapViewModel
import com.mitsudrive.features.sos.impl.repository.SosRepositoryImpl
import com.mitsudrive.features.sos.ui.screen.SosScreen
import com.mitsudrive.features.sos.ui.viewmodel.SosViewModel

// ==================== FEED ====================

@Composable
fun FeedRoute(
    onPostClick: (String) -> Unit,
    onCreatePost: () -> Unit
) {
    val repository = remember { FeedRepositoryImpl() }
    val viewModel: FeedViewModel = viewModel(
        factory = FeedViewModelFactory(repository)
    )
    
    // Загружаем ленту при первом запуске
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }
    
    FeedScreen(
        viewModel = viewModel,
        onCreatePost = onCreatePost,
        onPostClick = onPostClick
    )
}

// ==================== MAP ====================

@Composable
fun MapRoute() {
    val context = LocalContext.current
    val repository = remember { MapRepositoryImpl() }
    val locationManager = remember { LocationManager(context.applicationContext) }
    val viewModel: MapViewModel = viewModel(
        factory = MapViewModelFactory(repository, locationManager)
    )
    
    MapScreen(viewModel = viewModel)
}

// ==================== CHAT ====================

@Composable
fun ChatListRoute(
    onChatClick: (String) -> Unit
) {
    val repository = remember { ChatRepositoryImpl() }
    val viewModel: ChatListViewModel = viewModel(
        factory = ChatListViewModelFactory(repository)
    )
    
    // Загружаем чаты при первом запуске
    LaunchedEffect(Unit) {
        repository.createMockChatsForDemo()
    }
    
    ChatListScreen(
        viewModel = viewModel,
        onChatClick = onChatClick,
        onCreateChat = { }
    )
}

@Composable
fun ChatDetailRoute(
    chatId: String,
    onBack: () -> Unit
) {
    val repository = remember { ChatRepositoryImpl() }
    val viewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(repository, chatId)
    )
    
    ChatScreen(
        viewModel = viewModel,
        chatTitle = "Чат $chatId",
        onBack = onBack
    )
}

// ==================== GARAGE ====================

@Composable
fun GarageRoute() {
    val repository = remember { GarageRepositoryImpl() }
    val viewModel: GarageViewModel = viewModel(
        factory = GarageViewModelFactory(repository)
    )
    
    // Загружаем демо-данные при первом запуске
    LaunchedEffect(Unit) {
        repository.loadInitialData()
    }
    
    GarageScreen(viewModel = viewModel)
}

// ==================== SOS ====================

@Composable
fun SosRoute() {
    val context = LocalContext.current
    val repository = remember { SosRepositoryImpl() }
    val locationManager = remember { LocationManager(context.applicationContext) }
    val viewModel: SosViewModel = viewModel(
        factory = SosViewModelFactory(repository, locationManager)
    )
    
    // Загружаем активные SOS
    LaunchedEffect(Unit) {
        repository.loadActiveAlerts()
    }
    
    SosScreen(viewModel = viewModel)
}

// ==================== PROFILE ====================

@Composable
fun ProfileRoute() {
    Text(
        text = "Профиль",
        fontSize = 24.sp,
        color = TextPrimary
    )
}

// ==================== FACTORIES ====================

class FeedViewModelFactory(
    private val repository: com.mitsudrive.features.feed.api.FeedRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FeedViewModel(repository) as T
    }
}

class MapViewModelFactory(
    private val repository: com.mitsudrive.features.map.api.MapRepository,
    private val locationManager: LocationManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(repository, locationManager) as T
    }
}

class ChatListViewModelFactory(
    private val repository: com.mitsudrive.features.chat.api.ChatRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatListViewModel(repository) as T
    }
}

class ChatViewModelFactory(
    private val repository: com.mitsudrive.features.chat.api.ChatRepository,
    private val chatId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatViewModel(repository, chatId) as T
    }
}

class GarageViewModelFactory(
    private val repository: com.mitsudrive.features.garage.api.GarageRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GarageViewModel(repository) as T
    }
}

class SosViewModelFactory(
    private val repository: com.mitsudrive.features.sos.api.SosRepository,
    private val locationManager: LocationManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SosViewModel(repository, locationManager) as T
    }
}
