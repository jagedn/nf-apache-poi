package es.puravida.poi

import nextflow.Channel
import nextflow.plugin.Plugins
import nextflow.plugin.TestPluginDescriptorFinder
import nextflow.plugin.TestPluginManager
import nextflow.plugin.extension.PluginExtensionProvider
import org.pf4j.PluginDescriptorFinder
import spock.lang.Shared
import test.Dsl2Spec
import test.MockScriptRunner

import java.nio.file.Files
import java.nio.file.Path

class POIPluginTest extends Dsl2Spec{

    @Shared String pluginsMode

    def setup() {
        // reset previous instances
        PluginExtensionProvider.reset()
        // this need to be set *before* the plugin manager class is created
        pluginsMode = System.getProperty('pf4j.mode')
        System.setProperty('pf4j.mode', 'dev')
        // the plugin root should
        def root = Path.of('.').toAbsolutePath().normalize()
        def manager = new TestPluginManager(root){
            @Override
            protected PluginDescriptorFinder createPluginDescriptorFinder() {
                return new TestPluginDescriptorFinder(){
                    @Override
                    protected Path getManifestPath(Path pluginPath) {
                        return pluginPath.resolve('build/tmp/jar/MANIFEST.MF')
                    }
                }
            }
        }
        Plugins.init(root, 'dev', manager)
    }

    def cleanup() {
        Plugins.stop()
        PluginExtensionProvider.reset()
        pluginsMode ? System.setProperty('pf4j.mode',pluginsMode) : System.clearProperty('pf4j.mode')
    }

    def 'should starts' () {
        when:
        def SCRIPT = '''
            channel.of('hi!') 
            '''
        and:
        def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
        result.val == 'hi!'
        result.val == Channel.STOP
    }

    def 'should read an excel' () {
        when:
        def xls = Files.createTempFile("nf-apache-poi",".xlsx")
        xls.bytes = this.class.getResourceAsStream("/example.xlsx").bytes

        def SCRIPT = """
            include {fromExcel} from 'plugin/nf-apache-poi'
            channel.fromExcel('${xls.toAbsolutePath()}') | view 
            """
        and:
        def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
        result.val == ['Id', 'Description', 'Name', 'Price', 'Margin']
    }

    def 'should read a tab' () {
        when:
        def xls = Files.createTempFile("nf-apache-poi",".xlsx")
        xls.bytes = this.class.getResourceAsStream("/example.xlsx").bytes

        def SCRIPT = """
            include {fromExcel} from 'plugin/nf-apache-poi'
            channel.fromExcel('${xls.toAbsolutePath()}', [tab:'customers']) | view 
            """
        and:
        def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
        result.val == ['Id', 'Name', 'email', 'Created At']
    }

    def 'should send headers' () {
        when:
        def xls = Files.createTempFile("nf-apache-poi",".xlsx")
        xls.bytes = this.class.getResourceAsStream("/example.xlsx").bytes

        def SCRIPT = """
            include {fromExcel} from 'plugin/nf-apache-poi'
            channel.fromExcel('${xls.toAbsolutePath()}', [tab:'customers', headers:true]) | view 
            """
        and:
        def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
        result.val.Name == 'Customer1'
        result.val.Name == 'Customer2'
        result.val.Name == 'Customer3'
    }
}
