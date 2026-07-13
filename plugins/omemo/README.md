# AVHIRAL Spark OMEMO — Principal 0.14.2

Le journal 0.14.0 confirme que le déchiffrement fonctionne :

```text
RUNTIME DECRYPTED MESSAGE
body=Bonjour mon petit
```

Le défaut restant était entièrement dans l'intégration graphique Spark.

## Deux causes corrigées

### 1. Le fallback était encore affiché

Le listener Smack synchrone supprimait bien le corps, mais Spark avait déjà
programmé le traitement de la stanza dans son propre `Runnable`.

La 0.14.1 utilise maintenant les deux API natives de Spark :

```java
ChatManager.addMessageFilter(...)
ChatManager.addTranscriptWindowInterceptor(...)
```

Le filtre retire le corps avant la persistance de la conversation.  
L'intercepteur bloque l'entrée graphique de secours juste avant
`TranscriptWindow.insertMessage()`.

### 2. Le texte déchiffré n'était pas dessiné

L'ancienne méthode utilisait :

```java
room.addToTranscript(String, String, String, Date)
```

Dans Spark 3.0.2, cette méthode alimente uniquement la liste d'historique.
Elle ne dessine rien dans la fenêtre.

La 0.14.1 utilise maintenant :

```java
room.getTranscriptWindow().insertMessage(...)
```

puis ajoute le message à l'historique.

## Résultat attendu

Monal envoie :

```text
Bonjour mon petit
```

Spark affiche uniquement :

```text
david 🔒: Bonjour mon petit
```

Le texte suivant ne doit plus apparaître :

```text
[This message is OMEMO encrypted]
```


## Correctif 0.14.2

La version 0.14.1 utilisait :

```java
SparkManager.getSessionManager()
```

dans `IntegratedChatController.java`, mais l'import suivant manquait :

```java
import org.jivesoftware.spark.SparkManager;
```

La 0.14.2 ajoute cet import et renforce `verify-source.ps1` pour détecter
automatiquement cette régression avant compilation.
