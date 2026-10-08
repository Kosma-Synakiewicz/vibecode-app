package com.example.spotifyclone

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.Player

data class Song(
    val title: String,
    val uri: Uri
)

private val Background = Color(0xFF121212)
private val Card = Color(0xFF181818)
private val Green = Color(0xFF1DB954)

class MainActivity : ComponentActivity() {
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        player = ExoPlayer.Builder(this).build()

        setContent {
            MusicApp(player)
        }
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }
}

@Composable
fun MusicApp(player: ExoPlayer) {
    var songs by remember { mutableStateOf(emptyList<Song>()) }
    var selected by remember { mutableIntStateOf(-1) }
    var playing by remember { mutableStateOf(false) }
    var tab by remember { mutableIntStateOf(0) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            val newSongs = uris.map {
                Song(
                    title = it.lastPathSegment
                        ?.substringAfterLast("/")
                        ?.substringBeforeLast(".")
                        ?.ifBlank { "Unknown track" }
                        ?: "Unknown track",
                    uri = it
                )
            }
            songs = songs + newSongs
        }
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playing = isPlaying
            }
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                selected = player.currentMediaItemIndex
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Background,
            surface = Card,
            primary = Green
        )
    ) {
        Scaffold(
            containerColor = Background,
            bottomBar = {
                NavigationBar(containerColor = Color(0xFF090909)) {
                    NavigationBarItem(
                        selected = tab == 0,
                        onClick = { tab = 0 },
                        icon = { Icon(Icons.Default.Home, null) },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = tab == 1,
                        onClick = { tab = 1 },
                        icon = { Icon(Icons.Default.Search, null) },
                        label = { Text("Search") }
                    )
                    NavigationBarItem(
                        selected = tab == 2,
                        onClick = { tab = 2 },
                        icon = { Icon(Icons.Default.LibraryMusic, null) },
                        label = { Text("Library") }
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background)
                    .padding(padding)
            ) {
                if (tab == 0) {
                    HomeScreen(
                        songs = songs,
                        selected = selected,
                        playing = playing,
                        onImport = {
                            picker.launch(arrayOf("audio/*"))
                        },
                        onPlay = { index ->
                            selected = index
                            player.setMediaItem(MediaItem.fromUri(songs[index].uri))
                            player.prepare()
                            player.play()
                        },
                        onToggle = {
                            if (playing) player.pause() else player.play()
                        },
                        onNext = {
                            if (songs.isNotEmpty()) {
                                val next = if (selected + 1 < songs.size) selected + 1 else 0
                                selected = next
                                player.setMediaItem(MediaItem.fromUri(songs[next].uri))
                                player.prepare()
                                player.play()
                            }
                        }
                    )
                } else if (tab == 1) {
                    SearchScreen(songs) { index ->
                        selected = index
                        player.setMediaItem(MediaItem.fromUri(songs[index].uri))
                        player.prepare()
                        player.play()
                    }
                } else {
                    LibraryScreen(
                        songs = songs,
                        onImport = { picker.launch(arrayOf("audio/*")) },
                        onPlay = { index ->
                            selected = index
                            player.setMediaItem(MediaItem.fromUri(songs[index].uri))
                            player.prepare()
                            player.play()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    songs: List<Song>,
    selected: Int,
    playing: Boolean,
    onImport: () -> Unit,
    onPlay: (Int) -> Unit,
    onToggle: () -> Unit,
    onNext: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Good evening",
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onImport) {
                Icon(Icons.Default.Add, "Import", tint = Color.White)
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickCard("Liked Songs", Icons.Default.Favorite, Modifier.weight(1f))
            QuickCard("Your Music", Icons.Default.MusicNote, Modifier.weight(1f))
        }

        Spacer(Modifier.height(25.dp))

        Text(
            "Your tracks",
            color = Color.White,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(8.dp))

        if (songs.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.LibraryMusic,
                        null,
                        tint = Color.Gray,
                        modifier = Modifier.size(55.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("No music yet", color = Color.Gray)
                    Spacer(Modifier.height(10.dp))
                    Button(onClick = onImport) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Add MP3 files")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                itemsIndexed(songs) { index, song ->
                    SongRow(
                        song = song,
                        selected = index == selected,
                        onClick = { onPlay(index) }
                    )
                }
            }
        }

        if (selected >= 0 && selected < songs.size) {
            PlayerBar(
                song = songs[selected],
                playing = playing,
                onToggle = onToggle,
                onNext = onNext
            )
        }
    }
}

@Composable
fun QuickCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Row(
        modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Card)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Green, modifier = Modifier.size(30.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SongRow(song: Song, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0xFF282828)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (selected) Icons.Default.GraphicEq else Icons.Default.MusicNote,
                null,
                tint = if (selected) Green else Color.LightGray
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                song.title,
                color = if (selected) Green else Color.White,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text("Local audio", color = Color.Gray, fontSize = 13.sp)
        }
        Icon(Icons.Default.MoreVert, null, tint = Color.Gray)
    }
}

@Composable
fun PlayerBar(
    song: Song,
    playing: Boolean,
    onToggle: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color(0xFF202020))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    song.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text("Local audio", color = Color.Gray, fontSize = 12.sp)
            }
            IconButton(onClick = onToggle) {
                Icon(
                    if (playing) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
            }
            IconButton(onClick = onNext) {
                Icon(Icons.Default.SkipNext, null, tint = Color.White)
            }
        }
        LinearProgressIndicator(
            progress = { 0f },
            modifier = Modifier.fillMaxWidth(),
            color = Green,
            trackColor = Color.DarkGray
        )
    }
}

@Composable
fun SearchScreen(songs: List<Song>, onPlay: (Int) -> Unit) {
    var query by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Search", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(18.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search your music") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            singleLine = true
        )
        Spacer(Modifier.height(15.dp))

        val results = songs.withIndex().filter {
            it.value.title.contains(query, ignoreCase = true)
        }

        LazyColumn {
            itemsIndexed(results) { _, item ->
                SongRow(item.value, false) { onPlay(item.index) }
            }
        }
    }
}

@Composable
fun LibraryScreen(
    songs: List<Song>,
    onImport: () -> Unit,
    onPlay: (Int) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Your Library",
                color = Color.White,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onImport) {
                Icon(Icons.Default.Add, "Add music", tint = Color.White)
            }
        }

        Text(
            "${songs.size} tracks",
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(10.dp))

        LazyColumn {
            itemsIndexed(songs) { index, song ->
                SongRow(song, false) { onPlay(index) }
            }
        }
    }
}
