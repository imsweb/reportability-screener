# reportability-screener

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=imsweb_reportability-screener&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=imsweb_reportability-screener)
[![Integration](https://github.com/imsweb/reportability-screener/actions/workflows/integration.yml/badge.svg)](https://github.com/imsweb/reportability-screener/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.imsweb/reportability-screener/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.imsweb/reportability-screener)

A library for screening pathology reports for cancer incidence. 

The library is based on a list of POSITIVE, NEGATIVE and OTHER keyword groups. The list can either be supplied to the library or the internal list of 
[keywords](https://github.com/imsweb/reportability-screener/blob/main/src/main/resources/default.keyword.list.txt) can be used.

## Download

Java 8 is the minimum version required to use the library.

Maven

```xml

<dependency>
    <groupId>com.imsweb</groupId>
    <artifactId>reportability-screener</artifactId>
    <version>x.x.x</version>
</dependency>
```

Gradle

```groovy
compile 'com.imsweb:reportability-screener:x.x.x'
```

## Usage

First, create an instance of `ReportabilityScreener`. To use the default set of keywords:

```java
ReportabilityScreener screener = new ReportabilityScreenerBuilder().defaultKeywords().build();
```

To instantiate with custom keywords.

```java
ReportabilityScreenerBuilder builder = new ReportabilityScreenerBuilder();
builder.add("positive keyword", Group.POSITIVE);
builder.add("negative keyword", Group.NEGATIVE);
builder.add("other keyword", Group.OTHER);
ReportabilityScreener screener = builder.build();
```

To screen text and get information about reportability and the keywords used:

```java
ScreeningResult result = screener.screen("text to screen");
```

The `ScreeningResult` contains the `ReportabilityResult` as well as the positive, negative and other keywords that were found and contributed to the reportability.

To just check reportability:

```java
if(screener.isReportable("text to screen")){
    // process reportable report
}
```

## About SEER

The Surveillance, Epidemiology and End Results ([SEER](http://seer.cancer.gov)) Program is a premier source for cancer statistics in the United States. The SEER
Program collects information on incidence, prevalence and survival from specific geographic areas representing 28 percent of the US population and reports on all
these data plus cancer mortality data for the entire country.