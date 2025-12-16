package fasti.sh.eks;

import static fasti.sh.execute.serialization.Format.describe;
import static fasti.sh.execute.serialization.Format.name;

import com.fasterxml.jackson.core.type.TypeReference;
import fasti.sh.eks.stack.EksReleaseConf;
import fasti.sh.eks.stack.EksStack;
import fasti.sh.execute.util.ContextUtils;
import fasti.sh.execute.util.TemplateUtils;
import fasti.sh.model.main.Common;
import fasti.sh.model.main.Release;
import java.util.Map;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

/**
 * CDK application entry point for standalone EKS cluster deployment.
 *
 * <p>
 * Deploys a production-ready Amazon EKS cluster with VPC networking, managed addons, observability stack, and supporting infrastructure.
 */
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
          Environment
            .builder()
            .account(conf.release().common().account())
            .region(conf.release().common().region())
            .build())
        .description(
          describe(
            conf.platform(),
            String
              .format(
                "EKS cluster release [%s/%s] - Managed Kubernetes",
                conf.release().common().name(),
                conf.release().common().alias())))
        .tags(Common.Maps.from(conf.platform().tags(), conf.release().common().tags()))
        .build());

    app.synth();
  }

  private static Release<EksReleaseConf> get(App app) {
    var mappings = Map
      .<String, Object>ofEntries(
        Map.entry("deployment:tags", ContextUtils.parseTags(app, "deployment:tags")));
    var type = new TypeReference<Release<EksReleaseConf>>() {};
    return TemplateUtils.parseAs(app, "conf.mustache", mappings, type);
  }
}
