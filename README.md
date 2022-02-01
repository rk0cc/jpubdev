# [pub.dev](https://pub.dev) REST API implementation in Java

[![Test](https://github.com/Project-Will-Pub/jpubdev/actions/workflows/test.yml/badge.svg?branch=main)](https://github.com/Project-Will-Pub/jpubdev/actions/workflows/test.yml)
[![CodeQL](https://github.com/Project-Will-Pub/jpubdev/actions/workflows/codeql.yml/badge.svg?branch=main)](https://github.com/Project-Will-Pub/jpubdev/actions/workflows/codeql.yml)
[![Maven Central](https://img.shields.io/maven-central/v/xyz.rk0cc.willpub/jpubdev.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22xyz.rk0cc.willpub%22%20AND%20a:%22jpubdev%22)

This package aims to parse API result from pub.dev or other pub repository server information into
Java object.

This repository is replicating [pub_api_client's](https://github.com/leoafarias/pub_api_client) feature to Java which written in Dart originally.

## Supported feature

* Search package
  * Supported sort with different method
* Fetch package's information
  * Released versions
  * Options
  * Pub score and analystic report

## Install

**Maven POM**
```xml
<dependencies>
    <dependency>
        <groupId>xyz.rk0cc.willpub</groupId>
        <artifactId>jpubdev</artifactId>
        <version>1.0.3</version>
    </dependency>
</dependencies>
```
<p>* this package is bundled <a href="https://github.com/Project-Will-Pub/jpubspec">jpubspec</a> 1.2.2</p>

## Setup requirement

* Maven 3
* JDK 17 or higher

## License

BSD 3
