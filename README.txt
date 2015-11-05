# jmetrics
Java metrics is a annotations spec to extract metrics out of JMX beans, this code is designed to scale well when using large number of threads and cpus.
This allow to create definitions files for build jobs pipeline promotions and to setup production alerts based from code repositories.

- To build before you submit a PR
$ mvn clean install

- For contibutors run deploy to do a push to nexus servers.
$ mvn clean deploy -Dgpg.passphrase=[pathPhrase]

- All Pull requests need to pass continous integration before being merged.
  Please goto https://travis-ci.org/lafaspot/jmetrics
