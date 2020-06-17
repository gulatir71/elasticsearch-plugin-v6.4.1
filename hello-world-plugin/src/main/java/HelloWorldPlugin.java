//import org.apache.logging.log4j.util.Supplier;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.*;

public class HelloWorldPlugin extends Plugin implements ActionPlugin {
@Override
public List<RestHandler> getRestHandlers(final Settings settings,
                                         final RestController restController,
                                         final ClusterSettings clusterSettings,
                                         final IndexScopedSettings indexScopedSettings,
                                         final SettingsFilter settingsFilter,
                                         final IndexNameExpressionResolver indexNameExpressionResolver,
                                         final Supplier<DiscoveryNodes> nodesInCluster) {
        return Collections.singletonList(new HelloWorldRestAction(settings, restController));
    }
}