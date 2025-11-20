package fasti.sh.eks.stack;

import fasti.sh.execute.aws.eks.EksNestedStack;
import fasti.sh.execute.aws.vpc.NetworkNestedStack;
import lombok.Getter;
import software.amazon.awscdk.NestedStackProps;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

import static fasti.sh.execute.serialization.Format.describe;
import static fasti.sh.execute.serialization.Format.id;

@Getter
public class EksStack extends Stack {
  private final NetworkNestedStack network;
  private final EksNestedStack eks;

  public EksStack(Construct scope, EksReleaseConf conf, StackProps props) {
    super(scope, id("eks", conf.common().version()), props);

    this.network = new NetworkNestedStack(this, conf.common(), conf.vpc(),
      NestedStackProps.builder()
        .description(describe(conf.common(), "eks::network"))
        .build());

    this.eks = new EksNestedStack(this, conf.common(), conf.eks(), this.network().vpc(),
      NestedStackProps.builder()
        .description(describe(conf.common(), "eks::cluster"))
        .build());
  }
}
