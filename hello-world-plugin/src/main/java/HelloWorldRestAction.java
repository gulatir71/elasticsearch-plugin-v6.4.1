import java.io.IOException;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;

public class HelloWorldRestAction extends BaseRestHandler {

private static String NAME = "_hello";

@Inject
public HelloWorldRestAction(Settings settings, RestController restController) {
   super(settings);
   restController.registerHandler(RestRequest.Method.GET, "/" + NAME, this);
}

@Override
public String getName() {
   return NAME;
}

@Override
protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
   return channel -> {
      XContentBuilder builder = channel.newBuilder();
      builder.startObject().field("message", "HelloWorld").endObject();
      channel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));
   };
  }
}