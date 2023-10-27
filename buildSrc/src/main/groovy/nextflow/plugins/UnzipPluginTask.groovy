package nextflow.plugins
import org.gradle.api.tasks.Copy


class UnzipPluginTask extends Copy{

    UnzipPluginTask(){
        setGroup('nextflow')
        dependsOn project.tasks.zipPlugin
        outputs.upToDateWhen {false}
        from(project.tasks.zipPlugin.outputs.files.first())
        into "${System.getProperty("user.home")}/.nextflow/plugins/${project.name}-${project.version}"
    }

}
