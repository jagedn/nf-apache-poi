include { fromExcel } from 'plugin/nf-apache-poi'

workflow{
    Channel.fromExcel("example.xlsx") | view
}