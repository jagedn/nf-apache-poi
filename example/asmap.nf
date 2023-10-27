include { fromExcel } from 'plugin/nf-apache-poi'

workflow{
    Channel.fromExcel("example.xlsx", [tab:'customers', headers:true])
            | map { row ->
                row['Created At'] = (row['Created At'] as Date).format('yyyy-MM-dd')
                row
            }
            | view
}