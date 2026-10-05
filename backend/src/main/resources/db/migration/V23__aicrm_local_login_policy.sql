-- Project baseline: local prototype login uses a single password step.
-- A new deployment starts with MFA disabled; existing local accounts need no
-- forced password change on their next login.
update platform.system_config set value = 'false'::jsonb, updated_at = now()
where key in ('mfa.enabled', 'mfa.require_admin', 'mfa.allow_bootstrap');
update identity.account set must_change_password = false
where must_change_password = true;
