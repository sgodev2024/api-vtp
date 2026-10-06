export function hasPartnerRole(partner: Record<string, unknown>, role: string): boolean {
  const roles = partner.roles;
  return roles && typeof roles === "object" ? (roles as Record<string, unknown>)[role] === true : partner.kind === role;
}
