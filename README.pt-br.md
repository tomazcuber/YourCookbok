# YourCookbok - App de Busca de Receitas
[![pt-br](https://img.shields.io/badge/lang-pt--br-green.svg)](https://github.com/tomazcuber/YourCookbok/blob/readme/portuguese-version/README.pt-br.md)
[![en](https://img.shields.io/badge/lang-en-red.svg)](https://github.com/tomazcuber/YourCookbok/blob/readme/portuguese-version/README.md)

O YourCookbok é um aplicativo Android moderno, offline-first, construído para demonstrar uma arquitetura robusta, escalável e testável. Ele permite que os usuários pesquisem receitas, visualizem seus detalhes e mantenham uma coleção local de seus favoritos.

Este projeto foi desenvolvido como uma vitrine para entrevistas técnicas, priorizando a qualidade da arquitetura, a defensibilidade e a adesão às modernas práticas de desenvolvimento Android em detrimento de um amplo conjunto de recursos.

## Funcionalidades

- **Busca:** Encontre receitas da API TheMealDB com um campo de busca reativo e com debounce.
- **Offline-First:** Os resultados da busca são complementados de forma transparente com receitas salvas localmente quando a rede não está disponível.
- **Favoritos:** Salve e remova receitas para uma lista local de "favoritos" que está disponível offline.
- **Detalhes da Receita:** Visualize detalhes abrangentes da receita, incluindo ingredientes, medidas e instruções passo a passo.
- **UI Moderna:** Uma interface de usuário limpa, responsiva e intuitiva, construída inteiramente com Jetpack Compose e Material 3.

## Tecnologias Utilizadas

Este projeto utiliza uma pilha de tecnologias moderna e padrão da indústria:

- **UI:** 100% [Jetpack Compose](https://developer.android.com/jetpack/compose) para o desenvolvimento declarativo da UI.
- **Arquitetura:** Clean Architecture + MVVM (Model-View-ViewModel).
- **Injeção de Dependência:** [Hilt](https://dagger.dev/hilt/) para o gerenciamento de dependências.
- **Assincronismo:** [Kotlin Coroutines & Flow](https://kotlinlang.org/docs/coroutines-guide.html) para todas as operações assíncronas.
- **Rede:** [Retrofit](https://square.github.io/retrofit/) para consumir a API TheMealDB, com [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) para o parsing de JSON.
- **Banco de Dados Local:** [Room](https://developer.android.com/training/data-storage/room) para persistir as receitas favoritas.
- **Navegação:** [Jetpack Compose Navigation](https://developer.android.com/jetpack/compose/navigation) com um sistema de rotas serializável e type-safe.
- **Carregamento de Imagens:** [Coil](https://coil-kt.github.io/coil/) para o carregamento eficiente de imagens no Compose.
- **Testes Unitários:** [JUnit5](https://junit.org/junit5/), [MockK](https://mockk.io/) (para mocking) e [Strikt](https://strikt.io/) (para asserções).
- **Testes de UI:** Suíte de testes do Jetpack Compose (`createAndroidComposeRule`).

## Destaques e Decisões Arquiteturais

A arquitetura desta aplicação foi projetada para ser limpa, modular e altamente defensável. Os destaques de arquitetura mais importantes são:

### 1. Clean Architecture com MVVM

O projeto segue um padrão estrito de Clean Architecture, separando o código em três camadas principais:

- **Domain:** Contém os modelos de negócio principais (`Recipe.kt`), a interface `RecipeRepository` e os UseCases que são compartilhados entre as features (`GetSavedRecipesUseCase`, `DeleteRecipeUseCase`, `IsRecipeSavedUseCase`, `SaveRecipeUseCase`). Esta camada é Kotlin puro e não tem dependências externas.
- **Data:** Implementa a interface `RecipeRepository`. É responsável por todas as operações de dados, incluindo requisições de rede (`Retrofit`) e acesso ao banco de dados local (`Room`). Utiliza o padrão DTO (Data Transfer Object) para mapear as respostas da API para nossos modelos de domínio limpos.
- **Presentation (UI):** Construída com Jetpack Compose, esta camada consiste em ViewModels e telas Composable. Os ViewModels consomem UseCases da camada de domínio para conduzir o estado da UI.

### 2. Estrutura de Pacotes por Feature

Para aumentar a modularidade e a escalabilidade, o projeto utiliza uma estrutura de "pacotes por feature" (ex: `/search`, `/saved`, `/detail`). Isso mantém todo o código relacionado a uma única feature (UI, ViewModel, UseCases) em um só lugar, tornando a base de código mais fácil de navegar e manter.

### 3. Fluxo de Dados Unidirecional (UDF) na UI

A camada de UI segue estritamente os princípios inspirados no MVI:
- **Fonte Única da Verdade:** O ViewModel é o único proprietário do estado da tela, exposto como um `StateFlow<UiState>`.
- **Estado para Baixo, Eventos para Cima:** A UI observa este estado e envia todas as ações do usuário para o ViewModel através de uma classe `Event` selada, criando um fluxo de dados unidirecional e previsível.
- **Composables sem Estado:** Todas as telas são implementadas com um Composable "Route" com estado (que se conecta ao ViewModel) e um Composable "Screen" sem estado (que apenas renderiza a UI). Isso torna os componentes da UI altamente testáveis e visualizáveis.

### 4. Repositório Offline-First

O `RecipeRepository` foi projetado para ser offline-first. Quando um usuário busca por uma receita, o repositório primeiro tenta buscar dados atualizados da rede. Se a chamada de rede falhar, ele automaticamente recorre à busca no banco de dados local de receitas salvas, proporcionando uma experiência de usuário contínua e resiliente.

### 5. Navegação Type-Safe e Encapsulada

A navegação é implementada usando um sistema totalmente type-safe construído sobre `kotlinx.serialization`.
- **Rotas como Objetos:** Os destinos de navegação são definidos como objetos `@Serializable sealed class`, não strings, o que previne erros em tempo de execução por erros de digitação ou argumentos incorretos.
- **Encapsulamento:** Toda a lógica de navegação (o `NavHost`, `BottomNavigationBar` e as definições de rota) é encapsulada em um pacote dedicado `/presentation/navigation`, mantendo a `MainActivity` limpa e focada.
- **UI Consciente do Contexto:** A barra de navegação inferior é consciente do contexto, escondendo-se em telas de detalhes para fornecer uma hierarquia de navegação mais clara e intuitiva.

## Como Começar

1.  Clone o repositório.
2.  Abra o projeto em uma versão recente do Android Studio.
3.  Deixe o Gradle sincronizar as dependências.
4.  Execute a configuração `app` em um emulador ou dispositivo físico.

## Executando Testes

- Para executar todos os testes unitários, execute o comando `./gradlew testDebugUnitTest`.
- Para executar todos os testes instrumentados (UI), execute o comando `./gradlew connectedDebugAndroidTest`.
