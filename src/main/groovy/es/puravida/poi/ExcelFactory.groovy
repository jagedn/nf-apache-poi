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
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory

@Slf4j
@CompileStatic
class ExcelFactory extends PluginExtensionPoint{

    private Session session

    static final String TAB_NAME = "tab"
    static final String SKIP_NAME = "skip"
    static final String HEADERS_NAME = "headers"

    @Override
    protected void init(Session session) {
        this.session = session
    }

    @Factory
    DataflowWriteChannel fromExcel(String file, Map config=[:]) {
        final channel = CH.create()
        session.addIgniter((action) -> emitExcel(channel, file, config))
        return channel
    }

    private void emitExcel(DataflowWriteChannel channel, String file, Map config=[:]){
        def workbook = new XSSFWorkbookFactory().create(new File(file).newInputStream())
        def evaluator = workbook.creationHelper.createFormulaEvaluator()
        def sheet =
                config.containsKey(TAB_NAME) ?
                        config[TAB_NAME] instanceof String ?
                                workbook.getSheet(config[TAB_NAME] as String) : workbook.getSheetAt(config[TAB_NAME] as int)
                        :
                        workbook.getSheetAt(0)

        def skip = config.containsKey(SKIP_NAME) ? config[SKIP_NAME] as int : -1
        def asMap = config.containsKey(HEADERS_NAME) ? config[HEADERS_NAME] as boolean : false

        def headers = []
        sheet.rowIterator().eachWithIndex{ Row row, int i ->

            if( asMap && i==0){
                headers = rowToHeaders(row)
                return
            }
            if( skip != -1 && i+(asMap?1:0) < skip){
                return
            }

            def list = []
            def map = [:]
            row.cellIterator().eachWithIndex {cell, idx->
                def cellValue = evaluator.evaluate(cell)
                def value = null
                switch (cellValue?.cellType){
                    case CellType.NUMERIC:
                        value = DateUtil.isCellDateFormatted(cell) ? cell.dateCellValue :
                                cellValue.numberValue
                        break
                    case CellType.STRING:
                        value = cellValue.stringValue
                        break
                    case CellType.BOOLEAN:
                        value = cellValue.booleanValue
                        break
                    case CellType.BLANK:
                        value = ""
                        break
                }
                if(asMap){
                    map[headers[idx]] = value
                }else{
                    list << value
                }
            }
            channel.bind( asMap ? map : list);
        }
        channel.bind(Channel.STOP)
    }

    private List<String> rowToHeaders(Row row){
        def values = row.cellIterator().collect { cell ->
            cell.toString()
        }
        values
    }
}
