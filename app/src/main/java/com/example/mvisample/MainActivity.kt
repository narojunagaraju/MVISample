package com.example.mvisample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.example.mvisample.api.AnimalService
import com.example.mvisample.model.Animal
import com.example.mvisample.ui.theme.MviSampleTheme
import com.example.mvisample.view.MainIntent
import com.example.mvisample.view.MainState
import com.example.mvisample.view.MainViewModel
import com.example.mvisample.view.ViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProviders.of(this, ViewModelFactory(AnimalService.api))[MainViewModel::class.java]
        val onButtonClick: () -> Unit = {
            lifecycleScope.launch {
                mainViewModel.userIntent.send(MainIntent.FetchAnimals)
            }
        }

        setContent {
            MviSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen(viewModel = mainViewModel, onButtonClick = onButtonClick)
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel, onButtonClick: () -> Unit) {
    when (val state = viewModel.state.value) {
        is MainState.Idle -> IdleScreen(modifier = Modifier.fillMaxSize(), onButtonClick = onButtonClick)
        is MainState.Loading -> LoadingScreen(Modifier.fillMaxSize())
        is MainState.Animals -> AnimalsList(animals = state.animals)
        is MainState.Error -> {
            IdleScreen(modifier = Modifier.fillMaxSize(), onButtonClick = onButtonClick)
            Toast.makeText(LocalContext.current, "Error", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun IdleScreen(modifier: Modifier = Modifier, onButtonClick: () -> Unit) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Button(onClick = onButtonClick) {
            Text(text = "Fetch Animals")
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun AnimalsList(animals: List<Animal>) {
    LazyColumn {
        items(items = animals) {
            AnimalItem(modifier = Modifier.fillMaxSize(), animal = it)
            Divider(color = Color.LightGray, modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
fun AnimalItem(modifier: Modifier = Modifier, animal: Animal) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        val url = AnimalService.BASE_URL + animal.image
        val painter = rememberAsyncImagePainter(model = url)
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.FillHeight
        )
        Column(modifier = modifier.padding(4.dp)) {
            Text(text = animal.name, fontWeight = FontWeight.Bold)
            Text(text = animal.location)
        }
    }
}
