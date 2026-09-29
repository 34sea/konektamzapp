# Konekta - Plataforma de Emprego

Uma aplicação Android que aproxima pessoas que procuram emprego ou serviços de profissionais e empresas que procuram trabalhadores.

## Funcionalidades

### Autenticação e Registo
- Login e registo com email e senha
- Três tipos de conta: **Candidato**, **Empregador** e **Administrador**
- Dados persistidos em SQLite (Room)

### Candidato
- Pesquisar ofertas de emprego por palavra-chave
- Filtrar ofertas por categoria
- Ver detalhes das ofertas
- Candidatar-se a ofertas com carta de apresentação
- Acompanhar estado das candidaturas
- Ver ofertas no mapa (OpenStreetMap)
- Gerir perfil (competências, experiência)

### Empregador
- Criar ofertas de emprego
- Ver e gerir as suas ofertas
- Receber candidaturas
- Aceitar ou rejeitar candidatos
- Ver ofertas no mapa
- Gerir perfil da empresa

### Administrador
- Dashboard com estatísticas (utilizadores, ofertas, candidaturas)
- Gerir todos os utilizadores
- Gerir todas as ofertas
- Ver todas as candidaturas
- Remover utilizadores e ofertas

### Mapa (OpenStreetMap)
- Visualizar ofertas de emprego no mapa
- Marcadores com localização das ofertas
- Interação com marcadores para ver detalhes

## Credenciais Padrão

| Tipo | Email | Senha |
|------|-------|-------|
| Admin | `admin@konekta.co.mz` | `admin123` |

## Tecnologias

- **Kotlin** - Linguagem de programação
- **Jetpack Compose** - UI declarativa
- **Room** - Base de dados SQLite
- **OSMDroid** - OpenStreetMap para Android
- **Navigation Compose** - Navegação entre ecrãs
- **Material Design 3** - Design system
- **MVVM** - Arquitetura

## Estrutura do Projeto

```
app/src/main/java/com/example/konekta_mz_app/
├── data/
│   ├── local/
│   │   ├── entity/       # Entidades Room (User, JobOffer, JobApplication, Category)
│   │   ├── dao/          # DAOs para acesso a dados
│   │   ├── converter/    # Conversores de tipos Room
│   │   └── AppDatabase.kt # Base de dados Room
│   └── repository/       # Repositórios (Auth, Job, Category)
├── ui/
│   ├── screens/
│   │   ├── auth/         # Login e Registo
│   │   ├── home/         # Lista de ofertas
│   │   ├── map/          # Mapa OSM
│   │   ├── job/          # Detalhes e Criação de ofertas
│   │   ├── profile/      # Perfil do utilizador
│   │   ├── applications/ # Candidaturas
│   │   └── admin/        # Painel de administração
│   ├── navigation/       # Navegação
│   └── theme/            # Tema
├── viewmodel/            # ViewModels (MVVM)
├── KonektaApp.kt         # Application class
└── MainActivity.kt       # Atividade principal
```

## Como Executar

1. Abrir o projeto no Android Studio
2. Sincronizar o Gradle
3. Executar no emulador ou dispositivo físico

## Categorias Disponíveis

- Construção, Agricultura, Comércio, Serviços, Tecnologia, Educação, Saúde, Transportes, Hotelaria, Limpeza, Segurança, Outros
