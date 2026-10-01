# Floorball Fantasy

A Java desktop application for playing a fantasy league based on the Slovak floorball Extraliga. Managers draft real players, manage their rosters, and compete in fantasy matchups using statistics imported from the Slovak Floorball Association website.

The interface uses Swing with the FlatLaf One Dark theme, and league data is stored in MySQL.

## Features

- **League administration:** create teams, draw a random draft order, and start the draft.
- **Player draft:** each team makes 14 picks, taking turns in a repeating draft order.
- **Roster management:** assign players to position slots, swap compatible players, and add or drop players after the draft.
- **Player browser:** search by name and view average fantasy points per game.
- **Matchups and standings:** view the current opponent, player contributions, and team records.
- **Statistics import:** load players, match dates, and match statistics from `szfb.sk`.
- **Simulation controls:** simulate the draft and advance the stored season date by a day or a week.

## Technology

| Component | Technology |
| --- | --- |
| Language | Java 17 |
| Desktop interface | Swing |
| Theme | FlatLaf / FlatLaf IntelliJ Themes 2.6 |
| Persistence | MySQL via MySQL Connector/J 8.0.29 |
| HTML parsing | jsoup 1.8.1 |
| Additional dependency | org.json 20220320 |
| Dependency management | Maven |

## Getting started

### Requirements

- A **JDK supporting Java 17**, with `java` and `javac` available.
- Maven, or an IDE with Maven support.
- A MySQL database with the application's schema and initial settings.
- Internet access for Maven dependencies and statistics imports.
- A desktop environment capable of displaying Swing windows.

**Setup status:** the repository does not include a database schema, migrations, or seed script. A fresh clone cannot run a complete league until a compatible database has been provisioned. The source layout and one image path also require the adjustments described below.

### 1. Clone the repository

```sh
git clone https://github.com/samueljanco/floorball_fantasy.git
cd floorball_fantasy
```

### 2. Configure the database

Database connection settings are defined in [`src/main/database/DBConnection.java`](src/main/database/DBConnection.java). Replace `url`, `username`, and `password` with values for your own database, for example:

```java
public static final String url = "jdbc:mysql://localhost:3306/floorball_fantasy";
public static final String username = "your_database_user";
public static final String password = "your_database_password";
```

The application expects these tables:

| Table | Purpose |
| --- | --- |
| `Teams` | Fantasy teams, records, and draft order |
| `Players` | Player details, fantasy team ownership, and roster slots |
| `RealMatches` | Real match URLs and dates |
| `Matches` | Fantasy matchup pairings and date ranges |
| `PlayerStatLines` | Outfield player statistics and fantasy scores |
| `GoaliesStatLines` | Goalkeeper statistics and fantasy scores |
| `Settings` | League state, dates, and draft progress |

Use [`DatabaseController.java`](src/main/database/DatabaseController.java) and [`StatisticLoader.java`](src/main/database/StatisticLoader.java) as references for the expected columns and queries if reconstructing the schema. The table list above is not a complete schema definition.

The code reads a `Settings` row with `ID = 1`. For a new league, its initial `DraftState` should be `teams`, with `NextPick = 0` and `DraftPlace = 0`. Season dates are populated during the administrator's initial statistics import. Unassigned players are represented by `TeamID = 0`.

Connection settings are currently hardcoded; there is no environment variable or `.env` configuration support. The checked-in connection file contains credentials: replace them locally, avoid committing your own credentials, and rotate the existing credentials if they are still active.

### 3. Correct the empty-slot image path

In [`src/main/user/PlayerListView.java`](src/main/user/PlayerListView.java), the `addEmptyPlayerPanel` method loads `player.png` using an absolute path from the original developer's machine. Change that `new File(...)` argument to:

```java
new File("./src/main/icons/player.png")
```

Run the application from the repository root so its relative image paths resolve correctly.

### 4. Build and launch

The Java packages are named `main.java`, `main.admin`, `main.database`, and `main.user`, with their files under `src/main/`. Maven's default source directory, `src/main/java`, does not include all of these files.

#### Using an IDE

1. Open `pom.xml` as a Maven project and resolve its dependencies.
2. Select a Java 17 JDK for the project.
3. Set **`src` as the source root** so the directory layout matches the `main.*` packages. Remove any conflicting nested source-root setting for `src/main/java`.
4. Run **`main.java.Main`**, with the repository root as the working directory.

#### Using the command line

To build with Maven, add this configuration inside `<project>` in `pom.xml`:

```xml
<build>
    <sourceDirectory>src</sourceDirectory>
</build>
```

Also add this entry to the existing `<properties>` block to compile the Slovak text as UTF-8:

```xml
<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
```

From the repository root, compile and copy runtime dependencies:

```sh
mvn compile dependency:copy-dependencies
```

Launch on **Windows**:

```powershell
java -cp "target/classes;target/dependency/*" main.java.Main
```

Launch on **macOS or Linux**:

```sh
java -cp "target/classes:target/dependency/*" main.java.Main
```

The current project does not configure an executable JAR; use the classpath commands above to launch it.

## Playing a league

### Administrator

1. At the login screen, enter **`Admin`**. The name is case-sensitive.
2. Create a positive, even number of teams. Team names must contain at least five characters; reserve `Admin` for the administrator.
3. Press **Continue** to import players and real matches, initialize season dates, and create fantasy matchups. The interface may pause while the import runs.
4. When managers are ready, press **Proceed to draft lottery** to generate the draft order.
5. Press **Start Draft**. Managers make their picks from their team profiles.
6. Once the draft finishes, press **Refresh** to display the season controls.

For a demonstration, **Simulate draft (TEST)** assigns players to teams and ends the draft. **Add day** and **Add week** advance the date stored in the database. Statistics updates run when a team profile is opened and an update is due.

**Restart season deletes teams, players, matches, and recorded statistics**, then returns the league to team creation.

### Team manager

Log in using the name of a team created by the administrator. The application uses name-based access without passwords.

| Tab | What you can do |
| --- | --- |
| **TEAM** | View the roster. Click one position button and then another to swap compatible players or slots. |
| **MATCHUP** | View the current matchup, team scores, and player contributions. |
| **PLAYERS** | Search by name. After the draft, use the green `+` to add a free player or the red `-` to drop your player. A blue `*` indicates a player owned by another team. |
| **LEAGUE → Standings** | View team wins, draws, and losses. Standings are ordered by wins, then draws. |
| **LEAGUE → Draft** | When it is your turn, select an available player and press **Draft**. Use **Refresh** while waiting for the draft to start or for your turn. |

Each roster contains **14 slots**: one goalkeeper, six forwards, four defenders, and three bench spots. A new player needs a compatible free position or a free bench slot.

Player-name search is case-sensitive. Draft status refreshes manually. Use **Log Out** to return to the login screen; reopening a team profile reloads its data.

## Fantasy scoring

Scoring is defined in [`StatisticLoader.java`](src/main/database/StatisticLoader.java).

| Statistic | Points |
| --- | ---: |
| Outfield goal | +30 |
| Outfield assist | +20 |
| Goalkeeper save (`shotsOnGoal - goals conceded`) | +2 |
| Penalty value imported from the source statistics | -5 per unit |

```text
Outfield score = 30 × goals + 20 × assists - 5 × penalty
Goalkeeper score = 2 × (shots on goal - goals conceded) - 5 × penalty
```

For example, an outfield player with two goals, one assist, and a penalty value of two scores **70 points**.

## Project structure

```text
floorball_fantasy/
├── pom.xml                    # Maven dependencies and Java version
├── UserDocumentation.pdf      # User guide in Slovak
└── src/main/
    ├── java/                  # Application entry point and main window
    ├── admin/                 # Login, league setup, and season controls
    ├── database/              # JDBC access and statistics imports
    ├── user/                  # Draft, roster, players, matchups, and standings
    └── icons/                 # Player and jersey images
```

Some source files and database fields use the spelling `Roaster` instead of `Roster`; retain those names when configuring or querying the existing database.

