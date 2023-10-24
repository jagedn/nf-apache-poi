package es.puravida.poi

import groovy.util.logging.Slf4j
import nextflow.plugin.BasePlugin
import org.pf4j.PluginWrapper


@Slf4j
class ApachePOIPlugin extends BasePlugin{

    ApachePOIPlugin(PluginWrapper wrapper) {
        super(wrapper)
        initPlugin()
    }

    private void initPlugin(){
        log.info "Apache POI plugin initialized"
    }
}
