package fasti.sh.eks;

import static fasti.sh.execute.serialization.Format.describe;
import static fasti.sh.execute.serialization.Format.name;

import com.fasterxml.jackson.core.type.TypeReference;
import fasti.sh.eks.stack.EksReleaseConf;
import fasti.sh.eks.stack.EksStack;
import fasti.sh.execute.serialization.Mapper;
import fasti.sh.execute.serialization.Template;
import fasti.sh.model.main.Common;
import fasti.sh.model.main.Release;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class Launch {
  public static void main(final String[] args) {
    var app = new App();

    var conf = get(app);

    new EksStack(
      app, conf.release(),
      StackProps
        .builder()
        .stackName(name(conf.release().common().id(), "eks"))
        .env(
          Environment.builder()
            .account(conf.release().common().account())
            .region(conf.release().common().region())
            .build())
        .description(
          describe(
            conf.platform(),
            String.format(
              "EKS cluster release [%s/%s] - Managed Kubernetes",
              conf.release().common().name(),
              conf.release().common().alias())))
        .tags(Common.Maps.from(conf.platform().tags(), conf.release().common().tags()))
        .build());

    app.synth();
  }

  @SneakyThrows
  private static Release<EksReleaseConf> get(App app) {
    var parsed = Template.parse(
      app,
      "conf.mustache",
      Map.ofEntries(Map.entry("deployment:tags", tags(app))));
    var type = new TypeReference<Release<EksReleaseConf>>() {};
    return Mapper.get().readValue(parsed, type);
  }

  private static ArrayList<Map<String, String>> tags(App app) {
    var tags = app.getNode().getContext("deployment:tags");
    var results = new ArrayList<Map<String, String>>();
    if (tags instanceof List<?> tagList) {
      for (var tag : tagList) {
        if (tag instanceof Map<?, ?> tagMap) {
          var safeTagMap = new HashMap<String, String>();
          for (var entry : tagMap.entrySet()) {
            if (entry.getKey() instanceof String key && entry.getValue() instanceof String value) {
              safeTagMap.put(key, value);
            }
          }
          results.add(safeTagMap);
        }
      }
    }

    return results;
  }
}
