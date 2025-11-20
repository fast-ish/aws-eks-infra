package fasti.sh.eks.stack;

import fasti.sh.model.main.Common;
import fasti.sh.model.aws.eks.KubernetesConf;
import fasti.sh.model.aws.vpc.NetworkConf;

public record EksReleaseConf(
  Common common,
  NetworkConf vpc,
  KubernetesConf eks
) {}
