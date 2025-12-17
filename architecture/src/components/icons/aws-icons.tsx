import React from 'react';

interface IconProps {
  className?: string;
  size?: number;
}

export const EKSIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#FF9900"/>
    <circle cx="40" cy="40" r="18" stroke="white" strokeWidth="2" fill="none"/>
    <circle cx="40" cy="40" r="8" fill="white"/>
    <circle cx="40" cy="18" r="4" fill="white"/>
    <circle cx="40" cy="62" r="4" fill="white"/>
    <circle cx="18" cy="40" r="4" fill="white"/>
    <circle cx="62" cy="40" r="4" fill="white"/>
    <path d="M40 22V32" stroke="white" strokeWidth="2"/>
    <path d="M40 48V58" stroke="white" strokeWidth="2"/>
    <path d="M22 40H32" stroke="white" strokeWidth="2"/>
    <path d="M48 40H58" stroke="white" strokeWidth="2"/>
  </svg>
);

export const VPCIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#8C4FFF"/>
    <path d="M40 16L58 26V54L40 64L22 54V26L40 16Z" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M40 16V64" stroke="white" strokeWidth="2"/>
    <path d="M22 26L58 54" stroke="white" strokeWidth="2"/>
    <path d="M58 26L22 54" stroke="white" strokeWidth="2"/>
  </svg>
);

export const KubernetesIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#326CE5"/>
    <path d="M40 18L56 28V52L40 62L24 52V28L40 18Z" stroke="white" strokeWidth="2" fill="none"/>
    <circle cx="40" cy="40" r="8" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M40 18V32" stroke="white" strokeWidth="2"/>
    <path d="M40 48V62" stroke="white" strokeWidth="2"/>
    <path d="M24 28L32 36" stroke="white" strokeWidth="2"/>
    <path d="M56 28L48 36" stroke="white" strokeWidth="2"/>
    <path d="M24 52L32 44" stroke="white" strokeWidth="2"/>
    <path d="M56 52L48 44" stroke="white" strokeWidth="2"/>
  </svg>
);

export const KarpenterIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#0D47A1"/>
    <rect x="20" y="24" width="16" height="16" rx="2" stroke="white" strokeWidth="2" fill="none"/>
    <rect x="44" y="24" width="16" height="16" rx="2" stroke="white" strokeWidth="2" fill="none"/>
    <rect x="20" y="44" width="16" height="16" rx="2" stroke="white" strokeWidth="2" fill="none"/>
    <rect x="44" y="44" width="16" height="16" rx="2" stroke="white" strokeWidth="2" strokeDasharray="4 2" fill="none"/>
    <path d="M36 32H44" stroke="white" strokeWidth="2"/>
    <path d="M28 40V44" stroke="white" strokeWidth="2"/>
    <path d="M52 40V44" stroke="white" strokeWidth="2"/>
  </svg>
);

export const BottlerocketIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#232F3E"/>
    <path d="M40 16L28 56H52L40 16Z" fill="#FF9900" stroke="#FF9900" strokeWidth="2"/>
    <circle cx="40" cy="36" r="6" fill="white"/>
    <path d="M28 56L22 66H58L52 56" stroke="#FF9900" strokeWidth="2" fill="none"/>
    <path d="M32 62L36 58" stroke="white" strokeWidth="2"/>
    <path d="M48 62L44 58" stroke="white" strokeWidth="2"/>
  </svg>
);

export const IAMIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#DD344C"/>
    <circle cx="40" cy="32" r="10" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M24 58C24 48 32 42 40 42C48 42 56 48 56 58" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M40 22V42" stroke="white" strokeWidth="2"/>
    <circle cx="52" cy="28" r="4" fill="white"/>
    <path d="M50 28L52 30L54 26" stroke="#DD344C" strokeWidth="1.5"/>
  </svg>
);

export const ALBIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#8C4FFF"/>
    <circle cx="40" cy="40" r="16" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M28 40H52" stroke="white" strokeWidth="2"/>
    <path d="M40 28V52" stroke="white" strokeWidth="2"/>
    <circle cx="40" cy="40" r="4" fill="white"/>
    <path d="M16 40H24" stroke="white" strokeWidth="2"/>
    <path d="M56 40H64" stroke="white" strokeWidth="2"/>
    <path d="M40 16V24" stroke="white" strokeWidth="2"/>
    <path d="M40 56V64" stroke="white" strokeWidth="2"/>
  </svg>
);

export const CloudWatchIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#FF4F8B"/>
    <circle cx="40" cy="40" r="18" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M40 28V40L50 46" stroke="white" strokeWidth="2" strokeLinecap="round"/>
    <circle cx="40" cy="40" r="3" fill="white"/>
  </svg>
);

export const GrafanaIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#F46800"/>
    <circle cx="40" cy="40" r="18" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M26 50L34 38L42 44L50 32L54 36" stroke="white" strokeWidth="2" fill="none" strokeLinecap="round"/>
    <circle cx="34" cy="38" r="3" fill="white"/>
    <circle cx="42" cy="44" r="3" fill="white"/>
    <circle cx="50" cy="32" r="3" fill="white"/>
  </svg>
);

export const CertManagerIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#326CE5"/>
    <rect x="24" y="20" width="32" height="40" rx="4" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M32 32H48" stroke="white" strokeWidth="2"/>
    <path d="M32 40H48" stroke="white" strokeWidth="2"/>
    <path d="M32 48H44" stroke="white" strokeWidth="2"/>
    <circle cx="52" cy="52" r="10" fill="#4CAF50" stroke="white" strokeWidth="2"/>
    <path d="M47 52L50 55L57 48" stroke="white" strokeWidth="2"/>
  </svg>
);

export const HelmIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#0F1689"/>
    <circle cx="40" cy="40" r="18" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M40 22V58" stroke="white" strokeWidth="2"/>
    <path d="M22 40H58" stroke="white" strokeWidth="2"/>
    <circle cx="40" cy="40" r="6" fill="white"/>
    <path d="M30 30L50 50" stroke="white" strokeWidth="2"/>
    <path d="M50 30L30 50" stroke="white" strokeWidth="2"/>
  </svg>
);

export const CDKIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#232F3E"/>
    <path d="M24 24L40 16L56 24V40L40 48L24 40V24Z" fill="#FF9900" stroke="#FF9900" strokeWidth="2"/>
    <path d="M24 40L40 48L56 40V56L40 64L24 56V40Z" fill="#FF9900" fillOpacity="0.6" stroke="#FF9900" strokeWidth="2"/>
    <text x="40" y="38" textAnchor="middle" fill="white" fontSize="12" fontWeight="bold">CDK</text>
  </svg>
);

export const SecretsManagerIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#DD344C"/>
    <rect x="28" y="36" width="24" height="24" rx="4" stroke="white" strokeWidth="2" fill="none"/>
    <circle cx="40" cy="30" r="10" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M40 20V24" stroke="white" strokeWidth="2"/>
    <circle cx="40" cy="48" r="4" fill="white"/>
    <path d="M40 52V56" stroke="white" strokeWidth="2"/>
  </svg>
);

export const ExternalSecretsIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#7C3AED"/>
    <rect x="24" y="32" width="20" height="16" rx="2" stroke="white" strokeWidth="2" fill="none"/>
    <circle cx="34" cy="40" r="4" fill="white"/>
    <path d="M44 36L56 36" stroke="white" strokeWidth="2"/>
    <path d="M44 44L56 44" stroke="white" strokeWidth="2"/>
    <path d="M52 32L56 36L52 40" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M52 40L56 44L52 48" stroke="white" strokeWidth="2" fill="none"/>
  </svg>
);

export const TLSIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#16A34A"/>
    <rect x="28" y="36" width="24" height="20" rx="4" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M32 36V28C32 24 36 20 40 20C44 20 48 24 48 28V36" stroke="white" strokeWidth="2" fill="none"/>
    <circle cx="40" cy="46" r="4" fill="white"/>
    <path d="M40 50V52" stroke="white" strokeWidth="2"/>
  </svg>
);

export const SQSIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#FF4F8B"/>
    <rect x="24" y="28" width="32" height="24" rx="4" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M32 36H48" stroke="white" strokeWidth="2"/>
    <path d="M32 44H44" stroke="white" strokeWidth="2"/>
    <path d="M16 40H24" stroke="white" strokeWidth="2"/>
    <path d="M56 40H64" stroke="white" strokeWidth="2"/>
    <path d="M20 36L24 40L20 44" stroke="white" strokeWidth="2" fill="none"/>
  </svg>
);

export const EBSIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#3B48CC"/>
    <rect x="24" y="20" width="32" height="40" rx="4" stroke="white" strokeWidth="2" fill="none"/>
    <ellipse cx="40" cy="32" rx="12" ry="6" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M28 32V48" stroke="white" strokeWidth="2"/>
    <path d="M52 32V48" stroke="white" strokeWidth="2"/>
    <ellipse cx="40" cy="48" rx="12" ry="6" stroke="white" strokeWidth="2" fill="none"/>
  </svg>
);

export const CoreDNSIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#326CE5"/>
    <circle cx="40" cy="40" r="16" stroke="white" strokeWidth="2" fill="none"/>
    <text x="40" y="46" textAnchor="middle" fill="white" fontSize="14" fontWeight="bold">DNS</text>
  </svg>
);

export const VPCCNIIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#FF9900"/>
    <circle cx="28" cy="28" r="8" stroke="white" strokeWidth="2" fill="none"/>
    <circle cx="52" cy="28" r="8" stroke="white" strokeWidth="2" fill="none"/>
    <circle cx="28" cy="52" r="8" stroke="white" strokeWidth="2" fill="none"/>
    <circle cx="52" cy="52" r="8" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M36 28H44" stroke="white" strokeWidth="2"/>
    <path d="M28 36V44" stroke="white" strokeWidth="2"/>
    <path d="M52 36V44" stroke="white" strokeWidth="2"/>
    <path d="M36 52H44" stroke="white" strokeWidth="2"/>
  </svg>
);

export const PodIdentityIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#DD344C"/>
    <path d="M40 20L54 28V44L40 52L26 44V28L40 20Z" stroke="white" strokeWidth="2" fill="none"/>
    <circle cx="40" cy="36" r="6" fill="white"/>
    <path d="M32 50C32 46 36 44 40 44C44 44 48 46 48 50" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M40 58V64" stroke="white" strokeWidth="2"/>
    <circle cx="40" cy="58" r="4" stroke="white" strokeWidth="2" fill="none"/>
  </svg>
);

export const MetricsServerIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#326CE5"/>
    <rect x="20" y="48" width="8" height="12" fill="white"/>
    <rect x="32" y="40" width="8" height="20" fill="white"/>
    <rect x="44" y="32" width="8" height="28" fill="white"/>
    <rect x="56" y="24" width="8" height="36" fill="white"/>
    <path d="M16 60H64" stroke="white" strokeWidth="2"/>
  </svg>
);

export const ExternalDNSIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#FF9900"/>
    <circle cx="40" cy="40" r="16" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M32 40H48" stroke="white" strokeWidth="2"/>
    <path d="M40 32V48" stroke="white" strokeWidth="2"/>
    <path d="M16 40H24" stroke="white" strokeWidth="2"/>
    <path d="M56 40H64" stroke="white" strokeWidth="2"/>
    <text x="40" y="56" textAnchor="middle" fill="white" fontSize="8" fontWeight="bold">DNS</text>
  </svg>
);

export const ReloaderIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#14B8A6"/>
    <circle cx="40" cy="40" r="16" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M48 32L40 40L48 48" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M32 32L40 40L32 48" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M40 22V28" stroke="white" strokeWidth="2"/>
    <path d="M40 52V58" stroke="white" strokeWidth="2"/>
  </svg>
);

export const NodeGroupIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#FF9900"/>
    <rect x="20" y="24" width="18" height="14" rx="2" stroke="white" strokeWidth="2" fill="none"/>
    <rect x="42" y="24" width="18" height="14" rx="2" stroke="white" strokeWidth="2" fill="none"/>
    <rect x="20" y="42" width="18" height="14" rx="2" stroke="white" strokeWidth="2" fill="none"/>
    <rect x="42" y="42" width="18" height="14" rx="2" stroke="white" strokeWidth="2" fill="none"/>
    <circle cx="29" cy="31" r="3" fill="white"/>
    <circle cx="51" cy="31" r="3" fill="white"/>
    <circle cx="29" cy="49" r="3" fill="white"/>
    <circle cx="51" cy="49" r="3" fill="white"/>
  </svg>
);

export const ControlPlaneIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#FF9900"/>
    <rect x="24" y="24" width="32" height="32" rx="4" stroke="white" strokeWidth="2" fill="none"/>
    <circle cx="40" cy="40" r="8" fill="white"/>
    <path d="M40 24V32" stroke="white" strokeWidth="2"/>
    <path d="M40 48V56" stroke="white" strokeWidth="2"/>
    <path d="M24 40H32" stroke="white" strokeWidth="2"/>
    <path d="M48 40H56" stroke="white" strokeWidth="2"/>
  </svg>
);

export const OpenTelemetryIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#425CC7"/>
    <circle cx="40" cy="40" r="6" fill="white"/>
    <circle cx="24" cy="28" r="4" stroke="white" strokeWidth="2" fill="none"/>
    <circle cx="56" cy="28" r="4" stroke="white" strokeWidth="2" fill="none"/>
    <circle cx="24" cy="52" r="4" stroke="white" strokeWidth="2" fill="none"/>
    <circle cx="56" cy="52" r="4" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M28 30L36 38" stroke="white" strokeWidth="2"/>
    <path d="M52 30L44 38" stroke="white" strokeWidth="2"/>
    <path d="M28 50L36 42" stroke="white" strokeWidth="2"/>
    <path d="M52 50L44 42" stroke="white" strokeWidth="2"/>
  </svg>
);

export const PrometheusIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#E6522C"/>
    <circle cx="40" cy="40" r="16" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M40 28V40L48 48" stroke="white" strokeWidth="2"/>
    <circle cx="40" cy="40" r="4" fill="white"/>
    <path d="M28 52H52" stroke="white" strokeWidth="2"/>
    <path d="M24 56H56" stroke="white" strokeWidth="2"/>
  </svg>
);

export const LokiIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#F46800"/>
    <rect x="24" y="24" width="32" height="8" rx="2" fill="white"/>
    <rect x="24" y="36" width="32" height="8" rx="2" fill="white" fillOpacity="0.8"/>
    <rect x="24" y="48" width="32" height="8" rx="2" fill="white" fillOpacity="0.6"/>
  </svg>
);

export const TempoIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#F46800"/>
    <path d="M20 40L32 28L44 40L56 28L60 32" stroke="white" strokeWidth="3" fill="none" strokeLinecap="round"/>
    <path d="M20 52L32 40L44 52L56 40L60 44" stroke="white" strokeWidth="2" strokeOpacity="0.6" fill="none" strokeLinecap="round"/>
  </svg>
);

export const GoldilocksIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#FFB800"/>
    <rect x="20" y="44" width="12" height="16" rx="2" stroke="white" strokeWidth="2" fill="none"/>
    <rect x="34" y="32" width="12" height="28" rx="2" stroke="white" strokeWidth="2" fill="none"/>
    <rect x="48" y="20" width="12" height="40" rx="2" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M26 38L40 26L54 14" stroke="white" strokeWidth="2" strokeDasharray="4 2"/>
    <circle cx="40" cy="26" r="3" fill="white"/>
  </svg>
);

export const VeleroIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#1A73E8"/>
    <path d="M40 18L56 30V50L40 62L24 50V30L40 18Z" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M32 36L40 44L48 36" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M40 28V44" stroke="white" strokeWidth="2"/>
    <path d="M28 52H52" stroke="white" strokeWidth="2"/>
    <circle cx="40" cy="52" r="3" fill="white"/>
  </svg>
);

export const KyvernoIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#EF6C00"/>
    <path d="M40 18L56 28V52L40 62L24 52V28L40 18Z" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M40 28V52" stroke="white" strokeWidth="2"/>
    <path d="M28 40H52" stroke="white" strokeWidth="2"/>
    <circle cx="40" cy="40" r="8" stroke="white" strokeWidth="2" fill="none"/>
    <path d="M36 40L39 43L46 36" stroke="white" strokeWidth="2"/>
  </svg>
);

export const NodeTerminationHandlerIcon: React.FC<IconProps> = ({ className = '', size = 48 }) => (
  <svg className={className} width={size} height={size} viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect width="80" height="80" rx="8" fill="#D32F2F"/>
    <rect x="24" y="24" width="32" height="24" rx="4" stroke="white" strokeWidth="2" fill="none"/>
    <circle cx="32" cy="36" r="4" fill="white"/>
    <circle cx="48" cy="36" r="4" fill="white"/>
    <path d="M40 48V58" stroke="white" strokeWidth="2"/>
    <path d="M32 58H48" stroke="white" strokeWidth="2"/>
    <path d="M36 54L40 58L44 54" stroke="white" strokeWidth="2" fill="none"/>
  </svg>
);
