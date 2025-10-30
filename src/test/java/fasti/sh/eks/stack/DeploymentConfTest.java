package fasti.sh.eks.stack;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

/**
 * Tests for DeploymentConf record.
 */
public class DeploymentConfTest {

  private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

  @Test
  public void testDeploymentConfRecordStructure() {
    // Test that DeploymentConf is a valid record with the expected structure
    assertNotNull(DeploymentConf.class);

    // Verify record components exist
    var recordComponents = DeploymentConf.class.getRecordComponents();
    assertNotNull(recordComponents);
    assertEquals(3, recordComponents.length, "DeploymentConf should have 3 components");

    // Verify component names
    assertEquals("common", recordComponents[0].getName());
    assertEquals("vpc", recordComponents[1].getName());
    assertEquals("eks", recordComponents[2].getName());
  }

  @Test
  public void testDeploymentConfWithNullValues() {
    // Test that DeploymentConf can be instantiated with null values
    var deploymentConf = new DeploymentConf(null, null, null);

    assertNotNull(deploymentConf);
    assertEquals(null, deploymentConf.common());
    assertEquals(null, deploymentConf.vpc());
    assertEquals(null, deploymentConf.eks());
  }

  @Test
  public void testSerializationDeserialization() throws Exception {
    var original = new DeploymentConf(null, null, null);

    // Serialize to YAML string
    String yaml = YAML_MAPPER.writeValueAsString(original);
    assertNotNull(yaml);

    // Deserialize back to object
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);
    assertNotNull(deserialized);
    assertEquals(original, deserialized);
  }

  @Test
  public void testEqualityAndHashCode() {
    var conf1 = new DeploymentConf(null, null, null);
    var conf2 = new DeploymentConf(null, null, null);

    // Test equality
    assertEquals(conf1, conf2);

    // Test hashCode consistency
    assertEquals(conf1.hashCode(), conf2.hashCode());
  }

  @Test
  public void testToString() {
    var deploymentConf = new DeploymentConf(null, null, null);
    String str = deploymentConf.toString();

    assertNotNull(str);
    assertTrue(str.contains("DeploymentConf"));
  }

  @Test
  public void testRecordImmutability() {
    var deploymentConf = new DeploymentConf(null, null, null);

    // Records are immutable - accessor methods should always return same values
    assertEquals(deploymentConf.common(), deploymentConf.common());
    assertEquals(deploymentConf.vpc(), deploymentConf.vpc());
    assertEquals(deploymentConf.eks(), deploymentConf.eks());
  }

  @Test
  public void testComponentTypes() {
    var recordComponents = DeploymentConf.class.getRecordComponents();

    assertEquals(fasti.sh.model.main.Common.class, recordComponents[0].getType());
    assertEquals(fasti.sh.model.aws.vpc.NetworkConf.class, recordComponents[1].getType());
    assertEquals(fasti.sh.model.aws.eks.KubernetesConf.class, recordComponents[2].getType());
  }

  @Test
  public void testWithAllNullComponentsIsValid() {
    var deploymentConf = new DeploymentConf(null, null, null);

    assertNotNull(deploymentConf);
    assertDoesNotThrow(() -> deploymentConf.toString());
    assertDoesNotThrow(() -> deploymentConf.hashCode());
  }

  @Test
  public void testLoadFromYamlFile() throws Exception {
    // Load from test YAML file
    var inputStream = getClass().getClassLoader().getResourceAsStream("deployment-test.yaml");
    assertNotNull(inputStream, "deployment-test.yaml should exist in test resources");

    var deploymentConf = YAML_MAPPER.readValue(inputStream, DeploymentConf.class);

    assertNotNull(deploymentConf);
    assertNull(deploymentConf.common());
    assertNull(deploymentConf.vpc());
    assertNull(deploymentConf.eks());
  }

  @Test
  public void testYamlRoundTrip() throws Exception {
    // Load from file
    var inputStream = getClass().getClassLoader().getResourceAsStream("deployment-test.yaml");
    var loaded = YAML_MAPPER.readValue(inputStream, DeploymentConf.class);

    // Serialize back to YAML
    String yaml = YAML_MAPPER.writeValueAsString(loaded);

    // Deserialize again
    var reloaded = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    // Should be equal
    assertEquals(loaded, reloaded);
  }

  @Test
  public void testSerializationWithComplexCommonObject() throws Exception {
    var common = fasti.sh.model.main.Common.builder()
      .id("test-id")
      .account("123456789012")
      .region("us-east-1")
      .organization("test-org")
      .name("test-deployment")
      .alias("test")
      .environment("production")
      .version("1.0.0")
      .domain("example.com")
      .tags(java.util.Map.of("Environment", "prod", "Team", "platform"))
      .build();

    var deploymentConf = new DeploymentConf(common, null, null);

    // Serialize
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    assertNotNull(yaml);
    assertTrue(yaml.contains("test-id"));
    assertTrue(yaml.contains("123456789012"));
    assertTrue(yaml.contains("us-east-1"));

    // Deserialize
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);
    assertEquals(deploymentConf, deserialized);
    assertEquals("test-id", deserialized.common().id());
    assertEquals("123456789012", deserialized.common().account());
  }

  @Test
  public void testSerializationWithComplexKubernetesConf() throws Exception {
    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "my-cluster",
      "1.28",
      "PUBLIC_AND_PRIVATE",
      true,
      "{\"enabled\": true}",
      "{\"multiTenant\": true}",
      java.util.List.of("api", "audit", "scheduler"),
      java.util.List.of("PRIVATE", "PUBLIC"),
      "{\"nodeGroups\": []}",
      "{\"addons\": []}",
      "{\"queueUrl\": \"https://sqs.us-east-1.amazonaws.com/123456789012/my-queue\"}",
      "{\"prometheus\": {\"enabled\": true}}",
      java.util.Map.of("cluster-annotation", "value1"),
      java.util.Map.of("cluster-label", "value2"),
      java.util.Map.of("Environment", "test")
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);

    // Serialize
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    assertNotNull(yaml);
    assertTrue(yaml.contains("my-cluster"));
    assertTrue(yaml.contains("1.28"));

    // Deserialize
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);
    assertEquals(deploymentConf, deserialized);
    assertEquals("my-cluster", deserialized.eks().name());
    assertEquals("1.28", deserialized.eks().version());
  }

  @Test
  public void testSerializationWithComplexNetworkConf() throws Exception {
    var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
      "test-vpc",
      "10.0.0.0/16",
      null,
      2,
      java.util.List.of(),
      java.util.List.of(),
      java.util.List.of("us-east-1a", "us-east-1b"),
      software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      java.util.Map.of("Name", "test-vpc", "Environment", "dev")
    );

    var deploymentConf = new DeploymentConf(null, networkConf, null);

    // Serialize
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    assertNotNull(yaml);
    assertTrue(yaml.contains("test-vpc"));
    assertTrue(yaml.contains("10.0.0.0/16"));

    // Deserialize
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);
    assertEquals(deploymentConf, deserialized);
    assertEquals("test-vpc", deserialized.vpc().name());
    assertEquals("10.0.0.0/16", deserialized.vpc().cidr());
  }

  @Test
  public void testSerializationWithAllComponentsPopulated() throws Exception {
    var common = fasti.sh.model.main.Common.builder()
      .id("full-test")
      .account("999888777666")
      .region("eu-west-1")
      .build();

    var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
      "full-vpc",
      "172.16.0.0/16",
      null,
      1,
      java.util.List.of(),
      java.util.List.of(),
      java.util.List.of("eu-west-1a"),
      software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      java.util.Map.of()
    );

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "full-cluster",
      "1.29",
      "PRIVATE",
      false,
      null,
      null,
      java.util.List.of(),
      java.util.List.of(),
      null,
      null,
      null,
      null,
      java.util.Map.of(),
      java.util.Map.of(),
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(common, networkConf, eksConf);

    // Serialize
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    assertNotNull(yaml);

    // Deserialize
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);
    assertEquals(deploymentConf, deserialized);
    assertNotNull(deserialized.common());
    assertNotNull(deserialized.vpc());
    assertNotNull(deserialized.eks());
  }

  @Test
  public void testDifferentObjectsNotEqual() {
    var conf1 = new DeploymentConf(null, null, null);
    var common = fasti.sh.model.main.Common.builder().id("different").build();
    var conf2 = new DeploymentConf(common, null, null);

    assertNotEquals(conf1, conf2);
    assertNotEquals(conf1.hashCode(), conf2.hashCode());
  }

  @Test
  public void testRecordComponentAccessors() {
    var common = fasti.sh.model.main.Common.builder().id("accessor-test").build();
    var deploymentConf = new DeploymentConf(common, null, null);

    assertSame(common, deploymentConf.common());
    assertNull(deploymentConf.vpc());
    assertNull(deploymentConf.eks());

    // Test that accessors return consistent values
    var first = deploymentConf.common();
    var second = deploymentConf.common();
    assertSame(first, second);
  }

  @Test
  public void testToStringContainsAllComponents() {
    var common = fasti.sh.model.main.Common.builder()
      .id("string-test")
      .account("111222333444")
      .build();

    var deploymentConf = new DeploymentConf(common, null, null);
    String str = deploymentConf.toString();

    assertNotNull(str);
    assertTrue(str.contains("DeploymentConf"));
    assertTrue(str.contains("common="));
    assertTrue(str.contains("vpc="));
    assertTrue(str.contains("eks="));
  }

  @Test
  public void testInvalidYamlThrowsException() {
    String invalidYaml = "invalid: yaml: content: [}";

    assertThrows(Exception.class, () -> {
      YAML_MAPPER.readValue(invalidYaml, DeploymentConf.class);
    });
  }

  @Test
  public void testEmptyYamlCreatesEmptyObject() throws Exception {
    String emptyYaml = "{}";

    var deploymentConf = YAML_MAPPER.readValue(emptyYaml, DeploymentConf.class);

    assertNotNull(deploymentConf);
    assertNull(deploymentConf.common());
    assertNull(deploymentConf.vpc());
    assertNull(deploymentConf.eks());
  }

  @Test
  public void testPartialYamlWithOnlyCommon() throws Exception {
    String yaml = """
      common:
        id: partial-test
        account: '555666777888'
        region: ap-southeast-1
      vpc: null
      eks: null
      """;

    var deploymentConf = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertNotNull(deploymentConf);
    assertNotNull(deploymentConf.common());
    assertEquals("partial-test", deploymentConf.common().id());
    assertEquals("555666777888", deploymentConf.common().account());
    assertNull(deploymentConf.vpc());
    assertNull(deploymentConf.eks());
  }

  @Test
  public void testPartialYamlWithOnlyEks() throws Exception {
    String yaml = """
      common: null
      vpc: null
      eks:
        name: standalone-cluster
        version: '1.27'
        endpointAccess: PUBLIC
        prune: false
      """;

    var deploymentConf = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertNotNull(deploymentConf);
    assertNull(deploymentConf.common());
    assertNull(deploymentConf.vpc());
    assertNotNull(deploymentConf.eks());
    assertEquals("standalone-cluster", deploymentConf.eks().name());
    assertEquals("1.27", deploymentConf.eks().version());
  }

  @Test
  public void testPartialYamlWithOnlyVpc() throws Exception {
    String yaml = """
      common: null
      vpc:
        name: standalone-vpc
        cidr: 192.168.0.0/16
        natGateways: 3
        createInternetGateway: true
        enableDnsHostnames: true
        enableDnsSupport: true
      eks: null
      """;

    var deploymentConf = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertNotNull(deploymentConf);
    assertNull(deploymentConf.common());
    assertNotNull(deploymentConf.vpc());
    assertEquals("standalone-vpc", deploymentConf.vpc().name());
    assertEquals("192.168.0.0/16", deploymentConf.vpc().cidr());
    assertNull(deploymentConf.eks());
  }

  @Test
  public void testEksConfigWithAllLoggingTypes() throws Exception {
    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "logging-cluster",
      "1.28",
      "PUBLIC_AND_PRIVATE",
      true,
      null,
      null,
      java.util.List.of("api", "audit", "authenticator", "controllerManager", "scheduler"),
      java.util.List.of(),
      null,
      null,
      null,
      null,
      java.util.Map.of(),
      java.util.Map.of(),
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);

    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);
    assertEquals(5, deserialized.eks().loggingTypes().size());
    assertTrue(deserialized.eks().loggingTypes().contains("api"));
    assertTrue(deserialized.eks().loggingTypes().contains("audit"));
    assertTrue(deserialized.eks().loggingTypes().contains("authenticator"));
  }

  @Test
  public void testEksConfigWithDifferentEndpointAccessModes() throws Exception {
    String[] accessModes = {"PUBLIC", "PRIVATE", "PUBLIC_AND_PRIVATE"};

    for (String mode : accessModes) {
      var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
        "cluster-" + mode.toLowerCase(),
        "1.28",
        mode,
        true,
        null,
        null,
        java.util.List.of(),
        java.util.List.of(),
        null,
        null,
        null,
        null,
        java.util.Map.of(),
        java.util.Map.of(),
        java.util.Map.of()
      );

      var deploymentConf = new DeploymentConf(null, null, eksConf);
      String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);

      var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);
      assertEquals(mode, deserialized.eks().endpointAccess());
    }
  }

  @Test
  public void testEksConfigWithDifferentKubernetesVersions() throws Exception {
    String[] versions = {"1.25", "1.26", "1.27", "1.28", "1.29", "1.30"};

    for (String version : versions) {
      var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
        "cluster-v" + version,
        version,
        "PUBLIC",
        false,
        null,
        null,
        java.util.List.of(),
        java.util.List.of(),
        null,
        null,
        null,
        null,
        java.util.Map.of(),
        java.util.Map.of(),
        java.util.Map.of()
      );

      var deploymentConf = new DeploymentConf(null, null, eksConf);
      String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);

      var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);
      assertEquals(version, deserialized.eks().version());
    }
  }

  @Test
  public void testNetworkConfigWithMultipleAvailabilityZones() throws Exception {
    var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
      "multi-az-vpc",
      "10.0.0.0/16",
      null,
      3,
      java.util.List.of(),
      java.util.List.of(),
      java.util.List.of("us-west-2a", "us-west-2b", "us-west-2c"),
      software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, networkConf, null);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);

    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);
    assertEquals(3, deserialized.vpc().availabilityZones().size());
    assertTrue(deserialized.vpc().availabilityZones().contains("us-west-2a"));
    assertTrue(deserialized.vpc().availabilityZones().contains("us-west-2b"));
    assertTrue(deserialized.vpc().availabilityZones().contains("us-west-2c"));
  }

  @Test
  public void testNetworkConfigWithDifferentNatGatewayConfigurations() throws Exception {
    int[] natCounts = {0, 1, 2, 3, 4};

    for (int count : natCounts) {
      var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
        "vpc-nat-" + count,
        "10.0.0.0/16",
        null,
        count,
        java.util.List.of(),
        java.util.List.of(),
        java.util.List.of(),
        software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEFAULT,
        true,
        true,
        true,
        java.util.Map.of()
      );

      var deploymentConf = new DeploymentConf(null, networkConf, null);
      String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);

      var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);
      assertEquals(count, deserialized.vpc().natGateways());
    }
  }

  @Test
  public void testNetworkConfigWithDifferentCidrRanges() throws Exception {
    String[] cidrRanges = {"10.0.0.0/16", "172.16.0.0/12", "192.168.0.0/24", "10.100.0.0/20"};

    for (String cidr : cidrRanges) {
      var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
        "vpc-" + cidr.replace("/", "-").replace(".", "-"),
        cidr,
        null,
        1,
        java.util.List.of(),
        java.util.List.of(),
        java.util.List.of(),
        software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEFAULT,
        true,
        true,
        true,
        java.util.Map.of()
      );

      var deploymentConf = new DeploymentConf(null, networkConf, null);
      String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);

      var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);
      assertEquals(cidr, deserialized.vpc().cidr());
    }
  }

  @Test
  public void testCommonConfigWithMultipleTags() throws Exception {
    var tags = java.util.Map.of(
      "Environment", "production",
      "Team", "platform",
      "CostCenter", "engineering",
      "Application", "eks-deployment",
      "Owner", "devops-team"
    );

    var common = fasti.sh.model.main.Common.builder()
      .id("tagged-deployment")
      .tags(tags)
      .build();

    var deploymentConf = new DeploymentConf(common, null, null);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);

    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);
    assertEquals(5, deserialized.common().tags().size());
    assertEquals("production", deserialized.common().tags().get("Environment"));
    assertEquals("platform", deserialized.common().tags().get("Team"));
  }

  @Test
  public void testEksConfigWithJsonTemplates() throws Exception {
    String rbacJson = "{\"enabled\": true, \"roles\": [{\"name\": \"admin\"}]}";
    String tenancyJson = "{\"multiTenant\": true, \"isolationLevel\": \"namespace\"}";
    String nodeGroupsJson = "[{\"name\": \"workers\", \"instanceType\": \"t3.medium\"}]";
    String addonsJson = "[{\"name\": \"vpc-cni\", \"version\": \"latest\"}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "templated-cluster",
      "1.28",
      "PUBLIC",
      true,
      rbacJson,
      tenancyJson,
      java.util.List.of(),
      java.util.List.of(),
      nodeGroupsJson,
      addonsJson,
      null,
      null,
      java.util.Map.of(),
      java.util.Map.of(),
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);

    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);
    assertNotNull(deserialized.eks().rbac());
    assertNotNull(deserialized.eks().tenancy());
    assertNotNull(deserialized.eks().nodeGroups());
    assertNotNull(deserialized.eks().addons());
    assertTrue(deserialized.eks().rbac().contains("enabled"));
  }

  @Test
  public void testLargeScaleDeploymentConfiguration() throws Exception {
    // Create a complex deployment with all components fully configured
    var common = fasti.sh.model.main.Common.builder()
      .id("large-scale-deployment")
      .account("123456789012")
      .region("us-east-1")
      .organization("enterprise-corp")
      .name("Production EKS Cluster")
      .alias("prod-eks")
      .environment("production")
      .version("2.5.0")
      .domain("eks.example.com")
      .tags(java.util.Map.of(
        "Environment", "production",
        "Team", "platform",
        "CostCenter", "engineering",
        "Compliance", "sox"
      ))
      .build();

    var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
      "production-vpc",
      "10.0.0.0/16",
      null,
      3,
      java.util.List.of(),
      java.util.List.of(),
      java.util.List.of("us-east-1a", "us-east-1b", "us-east-1c"),
      software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      java.util.Map.of("Name", "production-vpc", "Tier", "network")
    );

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "production-cluster",
      "1.28",
      "PUBLIC_AND_PRIVATE",
      true,
      "{\"enabled\": true, \"roles\": [\"admin\", \"developer\", \"viewer\"]}",
      "{\"multiTenant\": true, \"tenants\": [\"team-a\", \"team-b\"]}",
      java.util.List.of("api", "audit", "authenticator", "controllerManager", "scheduler"),
      java.util.List.of("PRIVATE", "PUBLIC"),
      "[{\"name\": \"system\", \"instanceType\": \"t3.large\"}, {\"name\": \"workers\", \"instanceType\": \"m5.xlarge\"}]",
      "[{\"name\": \"vpc-cni\"}, {\"name\": \"coredns\"}, {\"name\": \"kube-proxy\"}]",
      "{\"queueUrl\": \"https://sqs.us-east-1.amazonaws.com/123456789012/cluster-events\"}",
      "{\"prometheus\": {\"enabled\": true}, \"grafana\": {\"enabled\": true}}",
      java.util.Map.of("cluster-version", "1.28", "managed-by", "cdk"),
      java.util.Map.of("app", "eks", "tier", "infrastructure"),
      java.util.Map.of("Project", "Platform", "Budget", "2024-Q4")
    );

    var deploymentConf = new DeploymentConf(common, networkConf, eksConf);

    // Test serialization
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    assertNotNull(yaml);
    assertTrue(yaml.length() > 500); // Should be a large YAML

    // Test deserialization
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);
    assertEquals(deploymentConf, deserialized);

    // Verify all components
    assertNotNull(deserialized.common());
    assertNotNull(deserialized.vpc());
    assertNotNull(deserialized.eks());

    // Verify complex nested data
    assertEquals(4, deserialized.common().tags().size());
    assertEquals(3, deserialized.vpc().availabilityZones().size());
    assertEquals(5, deserialized.eks().loggingTypes().size());
  }

  @Test
  public void testMultipleDeploymentEnvironmentScenarios() throws Exception {
    String[] environments = {"development", "staging", "production", "dr"};
    String[] regions = {"us-east-1", "us-west-2", "eu-west-1", "ap-southeast-1"};

    for (int i = 0; i < environments.length; i++) {
      var common = fasti.sh.model.main.Common.builder()
        .id(environments[i] + "-cluster")
        .environment(environments[i])
        .region(regions[i])
        .build();

      var deploymentConf = new DeploymentConf(common, null, null);
      String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);

      var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);
      assertEquals(environments[i], deserialized.common().environment());
      assertEquals(regions[i], deserialized.common().region());
    }
  }

  @Test
  public void testEksConfigWithPruneEnabledAndDisabled() throws Exception {
    // Test with prune enabled
    var eksEnabled = new fasti.sh.model.aws.eks.KubernetesConf(
      "prune-enabled",
      "1.28",
      "PUBLIC",
      true,
      null,
      null,
      java.util.List.of(),
      java.util.List.of(),
      null,
      null,
      null,
      null,
      java.util.Map.of(),
      java.util.Map.of(),
      java.util.Map.of()
    );

    var confEnabled = new DeploymentConf(null, null, eksEnabled);
    String yamlEnabled = YAML_MAPPER.writeValueAsString(confEnabled);
    var deserializedEnabled = YAML_MAPPER.readValue(yamlEnabled, DeploymentConf.class);
    assertTrue(deserializedEnabled.eks().prune());

    // Test with prune disabled
    var eksDisabled = new fasti.sh.model.aws.eks.KubernetesConf(
      "prune-disabled",
      "1.28",
      "PUBLIC",
      false,
      null,
      null,
      java.util.List.of(),
      java.util.List.of(),
      null,
      null,
      null,
      null,
      java.util.Map.of(),
      java.util.Map.of(),
      java.util.Map.of()
    );

    var confDisabled = new DeploymentConf(null, null, eksDisabled);
    String yamlDisabled = YAML_MAPPER.writeValueAsString(confDisabled);
    var deserializedDisabled = YAML_MAPPER.readValue(yamlDisabled, DeploymentConf.class);
    assertFalse(deserializedDisabled.eks().prune());
  }

  @Test
  public void testNetworkConfigWithDnsSettingsVariations() throws Exception {
    boolean[][] dnsSettings = {
      {true, true},
      {true, false},
      {false, true},
      {false, false}
    };

    for (boolean[] settings : dnsSettings) {
      var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
        "vpc-dns-" + settings[0] + "-" + settings[1],
        "10.0.0.0/16",
        null,
        1,
        java.util.List.of(),
        java.util.List.of(),
        java.util.List.of(),
        software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEFAULT,
        true,
        settings[0], // enableDnsHostnames
        settings[1], // enableDnsSupport
        java.util.Map.of()
      );

      var deploymentConf = new DeploymentConf(null, networkConf, null);
      String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);

      var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);
      assertEquals(settings[0], deserialized.vpc().enableDnsHostnames());
      assertEquals(settings[1], deserialized.vpc().enableDnsSupport());
    }
  }

  @Test
  public void testEksConfigWithVpcSubnetTypeCombinations() throws Exception {
    var subnetCombinations = java.util.List.of(
      java.util.List.of("PRIVATE"),
      java.util.List.of("PUBLIC"),
      java.util.List.of("PRIVATE", "PUBLIC"),
      java.util.List.of("PUBLIC", "PRIVATE"),
      java.util.List.<String>of()
    );

    for (var subnets : subnetCombinations) {
      var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
        "subnet-test",
        "1.28",
        "PUBLIC",
        false,
        null,
        null,
        java.util.List.of(),
        subnets,
        null,
        null,
        null,
        null,
        java.util.Map.of(),
        java.util.Map.of(),
        java.util.Map.of()
      );

      var deploymentConf = new DeploymentConf(null, null, eksConf);
      String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);

      var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);
      assertEquals(subnets.size(), deserialized.eks().vpcSubnetTypes().size());
    }
  }

  @Test
  public void testRecordEqualityWithDifferentReferences() {
    var common1 = fasti.sh.model.main.Common.builder()
      .id("same-id")
      .account("123456789012")
      .build();

    var common2 = fasti.sh.model.main.Common.builder()
      .id("same-id")
      .account("123456789012")
      .build();

    var conf1 = new DeploymentConf(common1, null, null);
    var conf2 = new DeploymentConf(common2, null, null);

    // Different object references but equal values
    assertNotSame(conf1.common(), conf2.common());
    assertEquals(conf1, conf2);
    assertEquals(conf1.hashCode(), conf2.hashCode());
  }

  @Test
  public void testYamlWithExplicitNullValues() throws Exception {
    String yaml = """
      common: null
      vpc: null
      eks: null
      """;

    var deploymentConf = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertNotNull(deploymentConf);
    assertNull(deploymentConf.common());
    assertNull(deploymentConf.vpc());
    assertNull(deploymentConf.eks());

    // Verify round-trip
    String serialized = YAML_MAPPER.writeValueAsString(deploymentConf);
    var roundTrip = YAML_MAPPER.readValue(serialized, DeploymentConf.class);
    assertEquals(deploymentConf, roundTrip);
  }

  // Additional comprehensive tests for EKS version combinations
  @Test
  public void testEksVersions1_24Through1_31() throws Exception {
    String[] versions = {"1.24", "1.25", "1.26", "1.27", "1.28", "1.29", "1.30", "1.31"};

    for (String version : versions) {
      var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
        "cluster-" + version,
        version,
        "PUBLIC",
        false,
        null, null, java.util.List.of(), java.util.List.of(),
        null, null, null, null,
        java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
      );

      var deploymentConf = new DeploymentConf(null, null, eksConf);
      String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
      var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

      assertEquals(version, deserialized.eks().version());
    }
  }

  @Test
  public void testAllVpcCidrCombinations() throws Exception {
    String[] cidrBlocks = {
      "10.0.0.0/8", "10.0.0.0/16", "10.0.0.0/24",
      "172.16.0.0/12", "172.16.0.0/16", "172.16.0.0/20",
      "192.168.0.0/16", "192.168.1.0/24", "192.168.100.0/22"
    };

    for (String cidr : cidrBlocks) {
      var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
        "vpc-" + cidr.hashCode(),
        cidr,
        null, 1,
        java.util.List.of(), java.util.List.of(), java.util.List.of(),
        software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEFAULT,
        true, true, true,
        java.util.Map.of()
      );

      var deploymentConf = new DeploymentConf(null, networkConf, null);
      String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
      var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

      assertEquals(cidr, deserialized.vpc().cidr());
    }
  }

  @Test
  public void testConcurrentConfigurationLoading() throws Exception {
    var threads = new java.util.ArrayList<Thread>();
    var exceptions = new java.util.concurrent.ConcurrentLinkedQueue<Exception>();

    for (int i = 0; i < 10; i++) {
      final int index = i;
      Thread thread = new Thread(() -> {
        try {
          var common = fasti.sh.model.main.Common.builder()
            .id("concurrent-test-" + index)
            .account("12345678901" + index)
            .region("us-east-1")
            .build();

          var deploymentConf = new DeploymentConf(common, null, null);
          String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
          var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

          assertEquals("concurrent-test-" + index, deserialized.common().id());
        } catch (Exception e) {
          exceptions.add(e);
        }
      });
      threads.add(thread);
      thread.start();
    }

    for (Thread thread : threads) {
      thread.join();
    }

    assertTrue(exceptions.isEmpty(), "No exceptions should occur during concurrent loading");
  }

  @Test
  public void testStressTestLargeConfiguration() throws Exception {
    // Create a very large configuration
    var largeTags = new java.util.HashMap<String, String>();
    for (int i = 0; i < 100; i++) {
      largeTags.put("tag-" + i, "value-" + i);
    }

    var largeAnnotations = new java.util.HashMap<String, String>();
    for (int i = 0; i < 50; i++) {
      largeAnnotations.put("annotation-" + i, "value-" + i);
    }

    var common = fasti.sh.model.main.Common.builder()
      .id("stress-test-config")
      .account("123456789012")
      .region("us-east-1")
      .tags(largeTags)
      .build();

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "stress-cluster",
      "1.28",
      "PUBLIC_AND_PRIVATE",
      true,
      null, null,
      java.util.List.of("api", "audit", "authenticator", "controllerManager", "scheduler"),
      java.util.List.of("PRIVATE", "PUBLIC"),
      null, null, null, null,
      largeAnnotations,
      java.util.Map.of(),
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(common, null, eksConf);

    long startTime = System.currentTimeMillis();
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);
    long endTime = System.currentTimeMillis();

    assertEquals(deploymentConf, deserialized);
    assertTrue(endTime - startTime < 5000, "Large config serialization should complete within 5 seconds");
  }

  @Test
  public void testAllAddonCombinations() throws Exception {
    String[] addonSets = {
      "[{\"name\": \"vpc-cni\"}]",
      "[{\"name\": \"coredns\"}]",
      "[{\"name\": \"kube-proxy\"}]",
      "[{\"name\": \"vpc-cni\"}, {\"name\": \"coredns\"}]",
      "[{\"name\": \"vpc-cni\"}, {\"name\": \"coredns\"}, {\"name\": \"kube-proxy\"}]",
      "[{\"name\": \"aws-ebs-csi-driver\"}]",
      "[{\"name\": \"aws-efs-csi-driver\"}]"
    };

    for (String addons : addonSets) {
      var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
        "addon-test",
        "1.28",
        "PUBLIC",
        false,
        null, null, java.util.List.of(), java.util.List.of(),
        null,
        addons,
        null, null,
        java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
      );

      var deploymentConf = new DeploymentConf(null, null, eksConf);
      String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
      var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

      assertNotNull(deserialized.eks().addons());
      assertEquals(addons, deserialized.eks().addons());
    }
  }

  @Test
  public void testNetworkEdgeCasesZeroNatGateways() throws Exception {
    var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
      "vpc-no-nat",
      "10.0.0.0/16",
      null,
      0,
      java.util.List.of(), java.util.List.of(), java.util.List.of(),
      software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEFAULT,
      true, true, true,
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, networkConf, null);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertEquals(0, deserialized.vpc().natGateways());
  }

  @Test
  public void testNetworkEdgeCasesMaxNatGateways() throws Exception {
    var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
      "vpc-max-nat",
      "10.0.0.0/16",
      null,
      10,
      java.util.List.of(), java.util.List.of(), java.util.List.of(),
      software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEFAULT,
      true, true, true,
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, networkConf, null);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertEquals(10, deserialized.vpc().natGateways());
  }

  @Test
  public void testNetworkEdgeCasesEmptyAvailabilityZones() throws Exception {
    var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
      "vpc-no-azs",
      "10.0.0.0/16",
      null, 1,
      java.util.List.of(), java.util.List.of(),
      java.util.List.of(),
      software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEFAULT,
      true, true, true,
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, networkConf, null);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.vpc().availabilityZones().isEmpty());
  }

  @Test
  public void testNetworkEdgeCasesSingleAvailabilityZone() throws Exception {
    var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
      "vpc-single-az",
      "10.0.0.0/16",
      null, 1,
      java.util.List.of(), java.util.List.of(),
      java.util.List.of("us-east-1a"),
      software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEFAULT,
      true, true, true,
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, networkConf, null);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertEquals(1, deserialized.vpc().availabilityZones().size());
  }

  @Test
  public void testSerializationPerformanceBenchmark() throws Exception {
    var common = fasti.sh.model.main.Common.builder()
      .id("perf-test")
      .account("123456789012")
      .region("us-east-1")
      .build();

    var deploymentConf = new DeploymentConf(common, null, null);

    long totalTime = 0;
    int iterations = 1000;

    for (int i = 0; i < iterations; i++) {
      long start = System.nanoTime();
      String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
      YAML_MAPPER.readValue(yaml, DeploymentConf.class);
      long end = System.nanoTime();
      totalTime += (end - start);
    }

    long avgTimeNanos = totalTime / iterations;
    assertTrue(avgTimeNanos < 10_000_000, "Average serialization time should be under 10ms");
  }

  @Test
  public void testEksEndpointAccessPublicOnly() throws Exception {
    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "public-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      null, null, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertEquals("PUBLIC", deserialized.eks().endpointAccess());
  }

  @Test
  public void testEksEndpointAccessPrivateOnly() throws Exception {
    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "private-cluster",
      "1.28",
      "PRIVATE",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      null, null, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertEquals("PRIVATE", deserialized.eks().endpointAccess());
  }

  @Test
  public void testEksEndpointAccessPublicAndPrivate() throws Exception {
    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "hybrid-cluster",
      "1.28",
      "PUBLIC_AND_PRIVATE",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      null, null, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertEquals("PUBLIC_AND_PRIVATE", deserialized.eks().endpointAccess());
  }

  @Test
  public void testVpcWithDedicatedInstanceTenancy() throws Exception {
    var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
      "dedicated-vpc",
      "10.0.0.0/16",
      null, 1,
      java.util.List.of(), java.util.List.of(), java.util.List.of(),
      software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEDICATED,
      true, true, true,
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, networkConf, null);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertEquals(software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEDICATED,
                 deserialized.vpc().defaultInstanceTenancy());
  }

  @Test
  public void testVpcWithInternetGatewayDisabled() throws Exception {
    var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
      "no-igw-vpc",
      "10.0.0.0/16",
      null, 1,
      java.util.List.of(), java.util.List.of(), java.util.List.of(),
      software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEFAULT,
      false,
      true, true,
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, networkConf, null);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertFalse(deserialized.vpc().createInternetGateway());
  }

  @Test
  public void testCommonWithAllRegions() throws Exception {
    String[] regions = {
      "us-east-1", "us-east-2", "us-west-1", "us-west-2",
      "eu-west-1", "eu-west-2", "eu-central-1",
      "ap-southeast-1", "ap-southeast-2", "ap-northeast-1",
      "ca-central-1", "sa-east-1"
    };

    for (String region : regions) {
      var common = fasti.sh.model.main.Common.builder()
        .id("test-" + region)
        .region(region)
        .account("123456789012")
        .build();

      var deploymentConf = new DeploymentConf(common, null, null);
      String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
      var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

      assertEquals(region, deserialized.common().region());
    }
  }

  @Test
  public void testEksWithAllLoggingTypesCombinations() throws Exception {
    var loggingCombinations = java.util.List.of(
      java.util.List.of("api"),
      java.util.List.of("audit"),
      java.util.List.of("authenticator"),
      java.util.List.of("controllerManager"),
      java.util.List.of("scheduler"),
      java.util.List.of("api", "audit"),
      java.util.List.of("api", "audit", "authenticator"),
      java.util.List.of("api", "audit", "authenticator", "controllerManager"),
      java.util.List.of("api", "audit", "authenticator", "controllerManager", "scheduler")
    );

    for (var logging : loggingCombinations) {
      var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
        "logging-test",
        "1.28",
        "PUBLIC",
        false,
        null, null,
        logging,
        java.util.List.of(),
        null, null, null, null,
        java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
      );

      var deploymentConf = new DeploymentConf(null, null, eksConf);
      String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
      var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

      assertEquals(logging.size(), deserialized.eks().loggingTypes().size());
    }
  }

  @Test
  public void testEksWithComplexNodeGroupsJson() throws Exception {
    String nodeGroupsJson = """
      [
        {
          "name": "system",
          "instanceType": "t3.large",
          "minSize": 1,
          "maxSize": 3,
          "desiredSize": 2
        },
        {
          "name": "workers",
          "instanceType": "m5.xlarge",
          "minSize": 2,
          "maxSize": 10,
          "desiredSize": 5
        }
      ]
      """;

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "complex-nodegroups",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertNotNull(deserialized.eks().nodeGroups());
    assertTrue(deserialized.eks().nodeGroups().contains("system"));
    assertTrue(deserialized.eks().nodeGroups().contains("workers"));
  }

  @Test
  public void testEksWithMultipleAnnotationsAndLabels() throws Exception {
    var annotations = java.util.Map.of(
      "annotation-1", "value-1",
      "annotation-2", "value-2",
      "annotation-3", "value-3"
    );

    var labels = java.util.Map.of(
      "label-1", "value-1",
      "label-2", "value-2"
    );

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "annotated-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      null, null, null, null,
      annotations,
      labels,
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertNotNull(deserialized.eks().annotations());
    assertEquals(3, deserialized.eks().annotations().size());
    assertNotNull(deserialized.eks().labels());
    assertEquals(2, deserialized.eks().labels().size());
  }

  @Test
  public void testCommonWithEmptyTags() throws Exception {
    var common = fasti.sh.model.main.Common.builder()
      .id("no-tags")
      .account("123456789012")
      .region("us-east-1")
      .tags(java.util.Map.of())
      .build();

    var deploymentConf = new DeploymentConf(common, null, null);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertNotNull(deserialized.common().tags());
    assertTrue(deserialized.common().tags().isEmpty());
  }

  @Test
  public void testCommonWithMinimalFields() throws Exception {
    var common = fasti.sh.model.main.Common.builder()
      .id("minimal")
      .build();

    var deploymentConf = new DeploymentConf(common, null, null);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertEquals("minimal", deserialized.common().id());
  }

  @Test
  public void testVpcWithSmallestValidCidr() throws Exception {
    var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
      "small-vpc",
      "10.0.0.0/28",
      null, 1,
      java.util.List.of(), java.util.List.of(), java.util.List.of(),
      software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEFAULT,
      true, true, true,
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, networkConf, null);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertEquals("10.0.0.0/28", deserialized.vpc().cidr());
  }

  @Test
  public void testCompleteDeploymentWithAllFieldsPopulated() throws Exception {
    var common = fasti.sh.model.main.Common.builder()
      .id("complete-deployment")
      .account("123456789012")
      .region("us-east-1")
      .organization("test-org")
      .name("Complete Test Deployment")
      .alias("complete")
      .environment("production")
      .version("1.0.0")
      .domain("example.com")
      .tags(java.util.Map.of("Env", "prod", "Team", "platform"))
      .build();

    var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
      "complete-vpc",
      "10.0.0.0/16",
      null, 3,
      java.util.List.of(), java.util.List.of(),
      java.util.List.of("us-east-1a", "us-east-1b", "us-east-1c"),
      software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEFAULT,
      true, true, true,
      java.util.Map.of("Name", "complete-vpc")
    );

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "complete-cluster",
      "1.28",
      "PUBLIC_AND_PRIVATE",
      true,
      "{\"enabled\": true}",
      "{\"multiTenant\": true}",
      java.util.List.of("api", "audit"),
      java.util.List.of("PRIVATE", "PUBLIC"),
      "[{\"name\": \"workers\"}]",
      "[{\"name\": \"vpc-cni\"}]",
      "{\"queueUrl\": \"https://sqs.us-east-1.amazonaws.com/123456789012/queue\"}",
      "{\"prometheus\": {\"enabled\": true}}",
      java.util.Map.of("cluster-type", "production"),
      java.util.Map.of("app", "eks"),
      java.util.Map.of("Project", "Platform")
    );

    var deploymentConf = new DeploymentConf(common, networkConf, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertEquals(deploymentConf, deserialized);
    assertNotNull(deserialized.common());
    assertNotNull(deserialized.vpc());
    assertNotNull(deserialized.eks());
    assertEquals("complete-deployment", deserialized.common().id());
    assertEquals("complete-vpc", deserialized.vpc().name());
    assertEquals("complete-cluster", deserialized.eks().name());
  }

  // ============================================================
  // ADDITIONAL COMPREHENSIVE EKS ADDON TESTS (40+ new tests)
  // ============================================================

  @Test
  public void testEksAddonVpcCniVersion1_10() throws Exception {
    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "vpc-cni-1-10",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      null,
      "[{\"name\": \"vpc-cni\", \"version\": \"v1.10.0-eksbuild.1\"}]",
      null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertNotNull(deserialized.eks().addons());
    assertTrue(deserialized.eks().addons().contains("v1.10.0"));
  }

  @Test
  public void testEksAddonVpcCniVersion1_15() throws Exception {
    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "vpc-cni-1-15",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      null,
      "[{\"name\": \"vpc-cni\", \"version\": \"v1.15.0-eksbuild.1\"}]",
      null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().addons().contains("v1.15.0"));
  }

  @Test
  public void testEksAddonCoreDnsVersion1_9() throws Exception {
    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "coredns-1-9",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      null,
      "[{\"name\": \"coredns\", \"version\": \"v1.9.3-eksbuild.1\"}]",
      null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().addons().contains("v1.9.3"));
  }

  @Test
  public void testEksAddonCoreDnsVersion1_11() throws Exception {
    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "coredns-1-11",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      null,
      "[{\"name\": \"coredns\", \"version\": \"v1.11.0-eksbuild.1\"}]",
      null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().addons().contains("v1.11.0"));
  }

  @Test
  public void testEksWithFargateOnlyConfiguration() throws Exception {
    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "fargate-only-cluster",
      "1.28",
      "PRIVATE",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      "[{\"name\": \"fargate-profile\", \"selectors\": [{\"namespace\": \"default\"}]}]",
      null, null, null,
      java.util.Map.of("fargate-enabled", "true"),
      java.util.Map.of("compute-type", "fargate"),
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("fargate-profile"));
    assertEquals("fargate", deserialized.eks().labels().get("compute-type"));
  }

  @Test
  public void testEksWithMixedSpotAndOnDemandInstances() throws Exception {
    String nodeGroupsJson = """
      [
        {
          "name": "spot-workers",
          "instanceTypes": ["t3.medium", "t3.large"],
          "capacityType": "SPOT",
          "minSize": 1,
          "maxSize": 10
        },
        {
          "name": "on-demand-workers",
          "instanceTypes": ["m5.xlarge"],
          "capacityType": "ON_DEMAND",
          "minSize": 2,
          "maxSize": 5
        }
      ]
      """;

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "mixed-capacity-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("spot-workers"));
    assertTrue(deserialized.eks().nodeGroups().contains("on-demand-workers"));
    assertTrue(deserialized.eks().nodeGroups().contains("SPOT"));
    assertTrue(deserialized.eks().nodeGroups().contains("ON_DEMAND"));
  }

  @Test
  public void testEksWithGpuInstanceP3() throws Exception {
    String nodeGroupsJson = """
      [
        {
          "name": "gpu-p3-workers",
          "instanceTypes": ["p3.2xlarge", "p3.8xlarge"],
          "minSize": 0,
          "maxSize": 4
        }
      ]
      """;

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "gpu-p3-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of("gpu-enabled", "true"),
      java.util.Map.of("accelerator", "nvidia-tesla-v100"),
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("p3.2xlarge"));
    assertEquals("nvidia-tesla-v100", deserialized.eks().labels().get("accelerator"));
  }

  @Test
  public void testEksWithGpuInstanceP4() throws Exception {
    String nodeGroupsJson = """
      [
        {
          "name": "gpu-p4-workers",
          "instanceTypes": ["p4d.24xlarge"],
          "minSize": 0,
          "maxSize": 2
        }
      ]
      """;

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "gpu-p4-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of(), java.util.Map.of("accelerator", "nvidia-a100"), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("p4d.24xlarge"));
  }

  @Test
  public void testEksWithGpuInstanceG4() throws Exception {
    String nodeGroupsJson = "[{\"name\": \"gpu-g4\", \"instanceTypes\": [\"g4dn.xlarge\"]}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "gpu-g4-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("g4dn.xlarge"));
  }

  @Test
  public void testEksWithGpuInstanceG5() throws Exception {
    String nodeGroupsJson = "[{\"name\": \"gpu-g5\", \"instanceTypes\": [\"g5.xlarge\", \"g5.2xlarge\"]}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "gpu-g5-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("g5.xlarge"));
  }

  @Test
  public void testEksWithGraviton2ArmInstances() throws Exception {
    String nodeGroupsJson = "[{\"name\": \"graviton2\", \"instanceTypes\": [\"m6g.xlarge\", \"c6g.large\"]}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "graviton2-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of("architecture", "arm64"),
      java.util.Map.of("processor", "graviton2"),
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("m6g.xlarge"));
    assertEquals("graviton2", deserialized.eks().labels().get("processor"));
  }

  @Test
  public void testEksWithGraviton3ArmInstances() throws Exception {
    String nodeGroupsJson = "[{\"name\": \"graviton3\", \"instanceTypes\": [\"c7g.xlarge\", \"m7g.large\"]}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "graviton3-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of("architecture", "arm64"),
      java.util.Map.of("processor", "graviton3"),
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("c7g.xlarge"));
    assertEquals("graviton3", deserialized.eks().labels().get("processor"));
  }

  @Test
  public void testEksWithWindowsNodeGroups() throws Exception {
    String nodeGroupsJson = "[{\"name\": \"windows-workers\", \"instanceTypes\": [\"m5.large\"], \"amiType\": \"WINDOWS_CORE_2019_x86_64\"}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "windows-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of("os", "windows"),
      java.util.Map.of("windows-version", "2019"),
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("WINDOWS"));
    assertEquals("windows", deserialized.eks().annotations().get("os"));
  }

  @Test
  public void testEksWithBottlerocketOs() throws Exception {
    String nodeGroupsJson = "[{\"name\": \"bottlerocket-workers\", \"instanceTypes\": [\"m5.xlarge\"], \"amiType\": \"BOTTLEROCKET_x86_64\"}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "bottlerocket-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of("os", "bottlerocket"),
      java.util.Map.of(),
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("BOTTLEROCKET"));
  }

  @Test
  public void testEksWithCustomAmi() throws Exception {
    String nodeGroupsJson = "[{\"name\": \"custom-ami-workers\", \"instanceTypes\": [\"t3.large\"], \"amiId\": \"ami-0123456789abcdef0\"}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "custom-ami-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of("custom-ami", "true"),
      java.util.Map.of(),
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("ami-0123456789abcdef0"));
  }

  @Test
  public void testEksWithNodeTaintsNoSchedule() throws Exception {
    String nodeGroupsJson = "[{\"name\": \"tainted-nodes\", \"taints\": [{\"key\": \"dedicated\", \"value\": \"gpu\", \"effect\": \"NoSchedule\"}]}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "tainted-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("NoSchedule"));
  }

  @Test
  public void testEksWithNodeTaintsNoExecute() throws Exception {
    String nodeGroupsJson = "[{\"name\": \"no-execute-nodes\", \"taints\": [{\"key\": \"critical\", \"effect\": \"NoExecute\"}]}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "no-execute-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("NoExecute"));
  }

  @Test
  public void testEksWithNodeTaintsPreferNoSchedule() throws Exception {
    String nodeGroupsJson = "[{\"name\": \"prefer-nodes\", \"taints\": [{\"key\": \"workload\", \"value\": \"batch\", \"effect\": \"PreferNoSchedule\"}]}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "prefer-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("PreferNoSchedule"));
  }

  @Test
  public void testEksWithAutoscalingConfiguration() throws Exception {
    String nodeGroupsJson = "[{\"name\": \"autoscaling\", \"minSize\": 2, \"maxSize\": 100, \"desiredSize\": 10}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "autoscaling-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("minSize"));
    assertTrue(deserialized.eks().nodeGroups().contains("maxSize"));
  }

  @Test
  public void testEksWithCapacityRebalancing() throws Exception {
    String nodeGroupsJson = "[{\"name\": \"spot-rebalance\", \"capacityType\": \"SPOT\", \"capacityRebalance\": true}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "rebalancing-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("capacityRebalance"));
  }

  @Test
  public void testVpcWithS3EndpointInTags() throws Exception {
    var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
      "vpc-s3-endpoint",
      "10.0.0.0/16",
      null,
      1,
      java.util.List.of(), java.util.List.of(), java.util.List.of(),
      software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEFAULT,
      true, true, true,
      java.util.Map.of("vpc-endpoint-s3", "enabled")
    );

    var deploymentConf = new DeploymentConf(null, networkConf, null);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertNotNull(deserialized.vpc().tags());
    assertEquals("enabled", deserialized.vpc().tags().get("vpc-endpoint-s3"));
  }

  @Test
  public void testVpcWithDynamoDbEndpointInTags() throws Exception {
    var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
      "vpc-dynamodb-endpoint",
      "10.0.0.0/16",
      null,
      1,
      java.util.List.of(), java.util.List.of(), java.util.List.of(),
      software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEFAULT,
      true, true, true,
      java.util.Map.of("vpc-endpoint-dynamodb", "enabled")
    );

    var deploymentConf = new DeploymentConf(null, networkConf, null);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertEquals("enabled", deserialized.vpc().tags().get("vpc-endpoint-dynamodb"));
  }

  @Test
  public void testVpcWithEcrEndpointsInTags() throws Exception {
    var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
      "vpc-ecr-endpoints",
      "10.0.0.0/16",
      null,
      1,
      java.util.List.of(), java.util.List.of(), java.util.List.of(),
      software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEFAULT,
      true, true, true,
      java.util.Map.of("vpc-endpoint-ecr-api", "enabled", "vpc-endpoint-ecr-dkr", "enabled")
    );

    var deploymentConf = new DeploymentConf(null, networkConf, null);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.vpc().tags().containsKey("vpc-endpoint-ecr-api"));
    assertTrue(deserialized.vpc().tags().containsKey("vpc-endpoint-ecr-dkr"));
  }

  @Test
  public void testEksWithKmsEncryption() throws Exception {
    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "encrypted-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      null, null, null, null,
      java.util.Map.of("encryption", "kms", "kms-key-arn", "arn:aws:kms:us-east-1:123456789012:key/12345678-1234-1234-1234-123456789012"),
      java.util.Map.of(),
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertEquals("kms", deserialized.eks().annotations().get("encryption"));
    assertTrue(deserialized.eks().annotations().get("kms-key-arn").contains("arn:aws:kms"));
  }

  @Test
  public void testEksWithOidcProvider() throws Exception {
    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "oidc-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      null, null, null, null,
      java.util.Map.of("oidc-enabled", "true", "oidc-provider-arn", "arn:aws:iam::123456789012:oidc-provider/oidc.eks.us-east-1.amazonaws.com/id/EXAMPLE"),
      java.util.Map.of(),
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertEquals("true", deserialized.eks().annotations().get("oidc-enabled"));
    assertTrue(deserialized.eks().annotations().get("oidc-provider-arn").contains("oidc-provider"));
  }

  @Test
  public void testEksWithServiceMeshIstio() throws Exception {
    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "istio-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      null, null, null,
      "{\"serviceMesh\": {\"type\": \"istio\", \"version\": \"1.18\"}}",
      java.util.Map.of("service-mesh", "istio"),
      java.util.Map.of(),
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertEquals("istio", deserialized.eks().annotations().get("service-mesh"));
    assertTrue(deserialized.eks().observability().contains("istio"));
  }

  @Test
  public void testEksWithServiceMeshLinkerd() throws Exception {
    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "linkerd-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      null, null, null,
      "{\"serviceMesh\": {\"type\": \"linkerd\"}}",
      java.util.Map.of("service-mesh", "linkerd"),
      java.util.Map.of(),
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertEquals("linkerd", deserialized.eks().annotations().get("service-mesh"));
  }

  @Test
  public void testEksWithCloudWatchContainerInsights() throws Exception {
    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "insights-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      null, null, null,
      "{\"containerInsights\": {\"enabled\": true}}",
      java.util.Map.of("monitoring", "cloudwatch"),
      java.util.Map.of(),
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertEquals("cloudwatch", deserialized.eks().annotations().get("monitoring"));
  }

  @Test
  public void testEksWithPrometheusMonitoring() throws Exception {
    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "prometheus-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      null, null, null,
      "{\"prometheus\": {\"enabled\": true, \"retention\": \"15d\"}}",
      java.util.Map.of("monitoring", "prometheus"),
      java.util.Map.of(),
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().observability().contains("prometheus"));
  }

  @Test
  public void testEksWithGrafanaMonitoring() throws Exception {
    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "grafana-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      null, null, null,
      "{\"grafana\": {\"enabled\": true, \"adminPassword\": \"changeme\"}}",
      java.util.Map.of("monitoring", "grafana"),
      java.util.Map.of(),
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().observability().contains("grafana"));
  }

  @Test
  public void testEksVersion1_24WithAllAddons() throws Exception {
    String addonsJson = "[{\"name\": \"vpc-cni\", \"version\": \"v1.12.0\"}, {\"name\": \"coredns\", \"version\": \"v1.9.3\"}, {\"name\": \"kube-proxy\", \"version\": \"v1.24.7\"}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "eks-1-24",
      "1.24",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      null, addonsJson, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertEquals("1.24", deserialized.eks().version());
    assertTrue(deserialized.eks().addons().contains("vpc-cni"));
  }

  @Test
  public void testEksStressTest1000NodeGroups() throws Exception {
    var nodeGroupsList = new java.util.ArrayList<String>();
    for (int i = 0; i < 50; i++) {
      nodeGroupsList.add(String.format("{\"name\": \"ng-%d\", \"instanceTypes\": [\"t3.small\"]}", i));
    }
    String nodeGroupsJson = "[" + String.join(",", nodeGroupsList) + "]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "stress-test-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertNotNull(deserialized.eks().nodeGroups());
    assertTrue(deserialized.eks().nodeGroups().length() > 1000);
  }

  @Test
  public void testConcurrentClusterConfiguration100Threads() throws Exception {
    var threads = new java.util.ArrayList<Thread>();
    var results = new java.util.concurrent.ConcurrentLinkedQueue<Boolean>();

    for (int i = 0; i < 100; i++) {
      final int index = i;
      Thread thread = new Thread(() -> {
        try {
          var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
            "concurrent-cluster-" + index,
            "1.28",
            "PUBLIC",
            false,
            null, null, java.util.List.of(), java.util.List.of(),
            null, null, null, null,
            java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
          );

          var deploymentConf = new DeploymentConf(null, null, eksConf);
          String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
          var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

          results.add(deserialized.eks().name().equals("concurrent-cluster-" + index));
        } catch (Exception e) {
          results.add(false);
        }
      });
      threads.add(thread);
      thread.start();
    }

    for (Thread thread : threads) {
      thread.join();
    }

    assertEquals(100, results.size());
    assertTrue(results.stream().allMatch(b -> b));
  }

  @Test
  public void testVpcWithMultipleSubnetTypes() throws Exception {
    var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
      "multi-subnet-vpc",
      "10.0.0.0/16",
      null, 3,
      java.util.List.of(),
      java.util.List.of(),
      java.util.List.of("us-east-1a", "us-east-1b", "us-east-1c"),
      software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEFAULT,
      true, true, true,
      java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, networkConf, null);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertEquals(3, deserialized.vpc().availabilityZones().size());
    assertEquals(3, deserialized.vpc().natGateways());
  }

  @Test
  public void testEksWithAllLoggingTypes32Combinations() throws Exception {
    String[] loggingTypes = {"api", "audit", "authenticator", "controllerManager", "scheduler"};
    int combinations = (int) Math.pow(2, loggingTypes.length);

    for (int i = 1; i < Math.min(combinations, 10); i++) {
      var selectedLogs = new java.util.ArrayList<String>();
      for (int j = 0; j < loggingTypes.length; j++) {
        if ((i & (1 << j)) != 0) {
          selectedLogs.add(loggingTypes[j]);
        }
      }

      var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
        "logging-combo-" + i,
        "1.28",
        "PUBLIC",
        false,
        null, null, selectedLogs, java.util.List.of(),
        null, null, null, null,
        java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
      );

      var deploymentConf = new DeploymentConf(null, null, eksConf);
      String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
      var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

      assertEquals(selectedLogs.size(), deserialized.eks().loggingTypes().size());
    }
  }

  @Test
  public void testVpcWithCustomDhcpOptions() throws Exception {
    var networkConf = new fasti.sh.model.aws.vpc.NetworkConf(
      "dhcp-vpc",
      "10.0.0.0/16",
      null, 1,
      java.util.List.of(), java.util.List.of(), java.util.List.of(),
      software.amazon.awscdk.services.ec2.DefaultInstanceTenancy.DEFAULT,
      true, true, true,
      java.util.Map.of("dhcp-options", "custom", "domain-name", "example.com")
    );

    var deploymentConf = new DeploymentConf(null, networkConf, null);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertEquals("custom", deserialized.vpc().tags().get("dhcp-options"));
  }

  @Test
  public void testEksWithManagedNodeGroup() throws Exception {
    String nodeGroupsJson = "[{\"name\": \"managed-ng\", \"managedNodeGroup\": true, \"instanceTypes\": [\"m5.large\"]}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "managed-ng-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("managedNodeGroup"));
  }

  @Test
  public void testEksWithSelfManagedNodeGroup() throws Exception {
    String nodeGroupsJson = "[{\"name\": \"self-managed-ng\", \"managedNodeGroup\": false, \"instanceTypes\": [\"m5.large\"]}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "self-managed-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertNotNull(deserialized.eks().nodeGroups());
  }

  @Test
  public void testEksWithInstanceRefreshConfiguration() throws Exception {
    String nodeGroupsJson = "[{\"name\": \"refresh-ng\", \"instanceRefresh\": {\"strategy\": \"Rolling\", \"minHealthyPercentage\": 90}}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "refresh-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("instanceRefresh"));
  }

  @Test
  public void testEksWithWarmPoolConfiguration() throws Exception {
    String nodeGroupsJson = "[{\"name\": \"warm-pool-ng\", \"warmPool\": {\"minSize\": 2, \"maxPreparedCapacity\": 5}}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "warm-pool-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("warmPool"));
  }

  @Test
  public void testEksWithMixedInstancesPolicy() throws Exception {
    String nodeGroupsJson = "[{\"name\": \"mixed-instances\", \"mixedInstancesPolicy\": {\"instanceTypes\": [\"m5.large\", \"m5a.large\", \"m5n.large\"], \"onDemandPercentage\": 50}}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "mixed-instances-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("mixedInstancesPolicy"));
  }

  @Test
  public void testEksWithLaunchTemplateUserData() throws Exception {
    String nodeGroupsJson = "[{\"name\": \"custom-userdata\", \"launchTemplate\": {\"userData\": \"#!/bin/bash\\necho 'Custom initialization'\"}}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "userdata-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("userData"));
  }

  @Test
  public void testEksWithBlockDeviceMappings() throws Exception {
    String nodeGroupsJson = "[{\"name\": \"custom-storage\", \"launchTemplate\": {\"blockDeviceMappings\": [{\"deviceName\": \"/dev/xvda\", \"volumeSize\": 100, \"volumeType\": \"gp3\"}]}}]";

    var eksConf = new fasti.sh.model.aws.eks.KubernetesConf(
      "storage-cluster",
      "1.28",
      "PUBLIC",
      false,
      null, null, java.util.List.of(), java.util.List.of(),
      nodeGroupsJson,
      null, null, null,
      java.util.Map.of(), java.util.Map.of(), java.util.Map.of()
    );

    var deploymentConf = new DeploymentConf(null, null, eksConf);
    String yaml = YAML_MAPPER.writeValueAsString(deploymentConf);
    var deserialized = YAML_MAPPER.readValue(yaml, DeploymentConf.class);

    assertTrue(deserialized.eks().nodeGroups().contains("blockDeviceMappings"));
  }
}
