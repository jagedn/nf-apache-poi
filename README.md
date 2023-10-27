# Nextflow Apache POI plugin (example)

A Nextflow plugin to read/write in Excel files using Apache POI

## Usage

```
include { fromExcel } from 'plugin/nf-apache-poi'

workflow{
    Channel.fromExcel("example.xlsx") 
        | view
} 
```

## Build

`./gradlew jsonPlugin`

It will compile and generate plugin artifacts at `build/plugin` directory

## Release a new version

- change `version` at `build.gradle`
- commit changes
- create a new tag `x.y.z` and push it

A GitHub action will create the release `x.y.z` and upload artifacts (json and zip)

## Example

Run a pipeline using a released version (0.0.1):

```shell
cd example

export NXF_PLUGINS_TEST_REPOSITORY="https://github.com/jagedn/nf-apache-poi/releases/download/0.0.1/nf-apache-poi-0.0.1-meta.json"
./nextflow run main.nf
...
[Id, Description, Name, Price]
[1.0, Description 1, Name 1, 1.2]
[2.0, Description 2, Name 2, 23.2]
[3.0, Description 3, Name 3, 123.0]
```
