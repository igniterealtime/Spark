# Sécurité

## Modèle de menace couvert

- interception réseau ;
- lecture des messages par le serveur XMPP ;
- usurpation d'un appareil non vérifié, signalée par les mécanismes OMEMO.

## Limites de l'alpha

- le stockage local OMEMO n'est pas encore enveloppé avec Windows DPAPI ;
- l'interface de vérification d'empreinte n'est pas encore intégrée ;
- les conversations de groupe ne sont pas implémentées ;
- les fichiers joints ne sont pas chiffrés par ce plugin ;
- aucune garantie n'est donnée avant audit.

## Règle critique

Le plugin refuse l'envoi lorsqu'une session OMEMO ne peut pas être établie. Il ne doit jamais faire de repli silencieux en texte clair.
