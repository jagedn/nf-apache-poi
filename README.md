# Nextflow Apache POI plugin (example)

This is an example to build a Nextflow plugin from scratch

## Build

`./gradlew jsonPlugin`

It will compile and generate plugin artifacts at `build/plugin` directory

## Release

TODO: create a GitHub action to publish artifacts

Manually: Create a release from GitHub project page. Add build/plugins artifacts to the release

## Example

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
