# Documentação de Prompts - Projeto TodoList-Auth

## Prompt 1: Configuração do Firebase Authentication

Preciso adicionar Firebase Authentication no meu projeto Android em Kotlin. Já baixei o arquivo google-services.json do console do Firebase. Me ajuda a configurar isso corretamente? O projeto usa Jetpack Compose e arquitetura MVVM.

/** Claude Sonnet 4.5 - início
* Prompt: Preciso adicionar Firebase Authentication no meu projeto Android em Kotlin
*/
```kotlin
// app/build.gradle.kts
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
}
```
/** Claude Sonnet 4.5 - final */

/** Estudante - início
* razão: Ajustei a versão do Firebase BOM de 32.7.0 para 33.6.0 pois estava dando conflito com outras dependências
*/
```kotlin
dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
}
```
/** Estudante - final */

---

## Prompt 2: Tela de Login com Jetpack Compose

Crie uma tela de login em Jetpack Compose para Android que tenha campos de email e senha, botão de login, botão para cadastro, indicador de loading, validação básica e integração com ViewModel usando StateFlow.

/** Claude Sonnet 4.5 - início
* Prompt: Crie uma tela de login em Jetpack Compose
*/
```kotlin
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToSignup: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        
        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }
    }
}
```
/** Claude Sonnet 4.5 - final */

/** Estudante - início
* razão: Adicionei validação de email e senha antes de chamar o ViewModel
*/
```kotlin
fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

Button(
    onClick = {
        if (isValidEmail(email) && password.length >= 6) {
            viewModel.login(email, password)
        }
    },
    enabled = isValidEmail(email) && password.length >= 6
) {
    Text("Entrar")
}
```
/** Estudante - final */

---

## Prompt 3: Erro de Compilação com Hilt

Tô com erro de compilação no Android Studio. Tô usando Hilt mas tá dando: "Unresolved reference: hiltViewModel" e "cannot find symbol class DaggerTodoApp_HiltComponents_SingletonC". Meu build.gradle só tem os plugins básicos. O que pode ser?

/** Gemini 2.5 Flash - início
* Prompt: Tô com erro de compilação no Android Studio. Tô usando Hilt
*/
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

dependencies {
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
}

kapt {
    correctErrorTypes = true
}
```
/** Gemini 2.5 Flash - final */

/** Estudante - início
* razão: Atualizei versão do Hilt de 2.48 para 2.50 e adicionei plugin no build.gradle do projeto
*/
```kotlin
// build.gradle.kts do projeto
plugins {
    id("com.google.dagger.hilt.android") version "2.50" apply false
}
```
/** Estudante - final */

---

## Prompt 4: Room Database não Salva Dados

Meu Room Database não tá salvando os dados. Criei TodoEntity, TodoDao, mas quando fecho o app os dados somem. O que tá faltando?

/** Claude Sonnet 4.5 - início
* Prompt: Meu Room Database não tá salvando os dados
*/
```kotlin
@Database(entities = [TodoEntity::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideTodoDatabase(@ApplicationContext context: Context): TodoDatabase {
        return Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            "todo_database"
        ).build()
    }
}
```
/** Claude Sonnet 4.5 - final */

/** Estudante - início
* razão: Adicionei campo userId e índice composto para filtrar tarefas por usuário
*/
```kotlin
@Entity(
    tableName = "todos",
    indices = [Index(value = ["userId", "timestamp"])]
)
data class TodoEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
```
/** Estudante - final */

---

## Prompt 5: Import Errado do ViewModel

Erro no Compose ao usar ViewModel com Hilt: "None of the following functions can be called with the arguments supplied". Meu código usa viewModel() mas não funciona.

/** Gemini 2.5 Flash - início
* Prompt: Erro no Compose ao usar ViewModel com Hilt
*/
```kotlin
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ListScreen(
    viewModel: ListViewModel = hiltViewModel()
) {
    // código
}
```
/** Gemini 2.5 Flash - final */

/** Estudante - início
* razão: Padronizei todos os imports do projeto removendo androidx.lifecycle.viewmodel.compose
*/
```kotlin
// Corrigido em:
// - LoginScreen.kt
// - SignupScreen.kt
// - ListScreen.kt
// - AddEditScreen.kt
import androidx.hilt.navigation.compose.hiltViewModel
```
/** Estudante - final */
