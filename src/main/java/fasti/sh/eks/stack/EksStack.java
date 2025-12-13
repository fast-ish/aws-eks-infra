package fasti.sh.eks.stack;

import static fasti.sh.execute.serialization.Format.describe;
import static fasti.sh.execute.serialization.Format.id;

import fasti.sh.execute.aws.eks.AddonsNestedStack;
import fasti.sh.execute.aws.eks.EksNestedStack;
import fasti.sh.execute.aws.eks.ObservabilityAddonsNestedStack;
import fasti.sh.execute.aws.eks.ObservabilityNestedStack;
import fasti.sh.execute.aws.vpc.NetworkNestedStack;
import lombok.Getter;
import software.amazon.awscdk.NestedStackProps;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

/**
 * Main CDK stack for EKS infrastructure deployment.
 *
 * <p>
 * This stack orchestrates the deployment of:
 * <ul>
 * <li>VPC networking via NetworkNestedStack</li>
 * <li>EKS cluster via EksNestedStack</li>
 * <li>Core EKS addons via CoreAddonsNestedStack (cert-manager, karpenter, load balancer, etc.)</li>
 * <li>Observability addons via ObservabilityAddonsNestedStack (alloy, grafana)</li>
 * <li>CloudWatch observability via ObservabilityNestedStack</li>
 * </ul>
 *
 * <p>
 * <b>Stack Separation Strategy:</b> EKS addons are split into separate nested stacks to:
 * <ul>
 * <li>Isolate Helm release failures - one addon stack failing won't affect others</li>
 * <li>Enable independent retry of failed addon deployments</li>
 * <li>Reduce blast radius of configuration changes</li>
 * </ul>
 */
@Getter
public class EksStack extends Stack {
  private final NetworkNestedStack network;
  private final EksNestedStack eks;
  private final AddonsNestedStack addons;
  private final ObservabilityAddonsNestedStack observabilityAddons;
  private final ObservabilityNestedStack observability;

  /**
   * Creates a new EksStack.
   *
   * @param scope
   *          the parent construct
   * @param conf
   *          the release configuration
   * @param props
   *          stack properties
   */
  public EksStack(Construct scope, EksReleaseConf conf, StackProps props) {
    super(scope, id("eks", conf.common().version()), props);

    this.network = new NetworkNestedStack(this, conf.common(), conf.vpc(),
      NestedStackProps
        .builder()
        .description(describe(conf.common(), "eks::network"))
        .build());

    this.eks = new EksNestedStack(this, conf.common(), conf.eks(), this.network.vpc(),
      NestedStackProps
        .builder()
        .description(describe(conf.common(), "eks::cluster"))
        .build());

    this.addons = new AddonsNestedStack(this, conf.common(), conf.eks(), this.eks.cluster(),
      NestedStackProps
        .builder()
        .description(describe(conf.common(), "eks::core-addons"))
        .build());

    this.observabilityAddons = new ObservabilityAddonsNestedStack(this, conf.common(), conf.eks(), this.eks.cluster(),
      NestedStackProps
        .builder()
        .description(describe(conf.common(), "eks::observability-addons"))
        .build());

    this.observability = new ObservabilityNestedStack(this, conf.common(), conf.eks().observability(),
      NestedStackProps
        .builder()
        .description(describe(conf.common(), "eks::observability"))
        .build());

    this.eks.addDependency(this.network);
    this.addons.addDependency(this.eks);
    this.observabilityAddons.addDependency(this.eks);
    this.observability.addDependency(this.eks);
  }
}
