package com.example.weatherapp
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.api.WeatherModel
import com.example.weatherapp.api.NetworkResponse
import androidx.compose.material.icons.filled.WaterDrop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {

    var city by remember {
        mutableStateOf("")
    }

    val weatherResult = viewModel.weatherResult.observeAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Water Drop Icon",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Blue)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Weather App", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 40.sp))
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedTextField(
                    textStyle = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f),
                    value = city,
                    onValueChange = {
                        city = it
                    },
                    label = {
                        Text(text = "Search for any location")
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = true
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            viewModel.getData(city)
                            keyboardController?.hide()
                        }
                    ),
                    trailingIcon = {
                        IconButton(onClick = {
                            viewModel.getData(city)
                            keyboardController?.hide()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search for any location"
                            )
                        }
                    }
                )
            }

            when(val result = weatherResult.value) {
                is NetworkResponse.Error -> {
                    Text(text = result.message, color = Color.Red)
                }
                NetworkResponse.Loading -> {
                    CircularProgressIndicator()
                }
                is NetworkResponse.Success -> {
                    WeatherDetails(data = result.data)
                }
                null -> {
                    Text(
                        text = "Search Up a Location",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherDetails(data: WeatherModel, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(8.dp, shape = RoundedCornerShape(16.dp), spotColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xff4F5166))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location icon",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = data.location.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = data.location.country,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${data.current.temp_c}°C",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            AsyncImage(
                modifier = Modifier
                    .size(128.dp)
                    .graphicsLayer(alpha = animatedAlpha),
                model = "https:${data.current.condition.icon}".replace("64x64", "128x128"),
                contentDescription = "Condition icon"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = data.current.condition.text,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.elevatedCardElevation(4.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    WeatherInfoRow(label = "Humidity", value = "${data.current.humidity}%")
                    Spacer(modifier = Modifier.height(8.dp))
                    WeatherInfoRow(label = "Wind Speed", value = "${data.current.wind_kph} km/h")
                    Spacer(modifier = Modifier.height(8.dp))
                    WeatherInfoRow(label = "UV Index", value = data.current.uv)
                    Spacer(modifier = Modifier.height(8.dp))
                    WeatherInfoRow(label = "Precipitation", value = "${data.current.precip_mm} mm")
                    Spacer(modifier = Modifier.height(8.dp))
                    WeatherInfoRow(label = "Local Time", value = data.location.localtime.split(" ")[1])
                    Spacer(modifier = Modifier.height(8.dp))
                    WeatherInfoRow(label = "Local Date", value = data.location.localtime.split(" ")[0])
                }
            }
        }
    }
}

@Composable
fun WeatherInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}