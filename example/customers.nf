include { fromExcel } from 'plugin/nf-apache-poi'

workflow{
    Channel.fromExcel("example.xlsx", [tab:'customers'])
            | map { row -> row.collect{ cell -> "$cell is $cell.class" } }
            | view
}