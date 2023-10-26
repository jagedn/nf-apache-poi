package es.puravida.poi

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import nextflow.Session
import nextflow.plugin.extension.PluginExtensionPoint
import groovyx.gpars.dataflow.DataflowReadChannel
import groovyx.gpars.dataflow.DataflowWriteChannel
import nextflow.Channel
import nextflow.Session
import nextflow.extension.CH
import nextflow.extension.DataflowHelper
import nextflow.plugin.extension.Factory
import nextflow.plugin.extension.Function
import nextflow.plugin.extension.Operator
import nextflow.plugin.extension.PluginExtensionPoint
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory

@Slf4j
@CompileStatic
class ExcelFactory extends PluginExtensionPoint{

    private Session session

    @Override
    protected void init(Session session) {
        this.session = session
    }

    @Factory
    DataflowWriteChannel fromExcel(String file) {
        final channel = CH.create()
        session.addIgniter((action) -> emitExcel(channel, file))
        return channel
    }

    private void emitExcel(DataflowWriteChannel channel, String file){
        def workbook = new XSSFWorkbookFactory().create(new File(file).newInputStream())
        def evaluator = workbook.creationHelper.createFormulaEvaluator()
        def sheet = workbook.getSheetAt(0)
        for( Row row : sheet) {
            def values = []
            row.cellIterator().each {cell->
                def cellValue = evaluator.evaluate(cell)
                switch (cellValue.cellType){
                    case CellType.NUMERIC:
                        values << cellValue.numberValue
                        break
                    case CellType.STRING:
                        values << cellValue.stringValue
                        break
                    case CellType.BOOLEAN:
                        values << cellValue.booleanValue
                        break
                    case CellType.BLANK:
                        values << ""
                        break
                }
            }
            channel.bind(values);
        }
        channel.bind(Channel.STOP)
    }

}
