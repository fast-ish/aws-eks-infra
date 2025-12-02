package fasti.sh.eks.stack;

import static fasti.sh.execute.serialization.Format.describe;
import static fasti.sh.execute.serialization.Format.id;

import fasti.sh.execute.aws.eks.EksNestedStack;
import fasti.sh.execute.aws.eks.ObservabilityNestedStack;
import fasti.sh.execute.aws.vpc.NetworkNestedStack;
import lombok.Getter;
import software.amazon.awscdk.NestedStackProps;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

@Getter
public class EksStack extends Stack {
  private final NetworkNestedStack network;
  private final EksNestedStack eks;
  private final ObservabilityNestedStack observability;

  public EksStack(Construct scope, EksReleaseConf conf, StackProps props) {
    super(scope, id("eks", conf.common().version()), props);

    this.network = new NetworkNestedStack(this, conf.common(), conf.vpc(),
      NestedStackProps
        .builder()
        .description(describe(conf.common(), "eks::network"))
        .build());

    this.eks = new EksNestedStack(this, conf.common(), conf.eks(), this.network().vpc(),
      NestedStackProps
        .builder()
        .description(describe(conf.common(), "eks::cluster"))
        .build());

    this.observability = new ObservabilityNestedStack(this, conf.common(), conf.eks().observability(),
      NestedStackProps
        .builder()
        .description(describe(conf.common(), "eks::observability"))
        .build());

    this.observability().addDependency(this.eks());
  }
}
