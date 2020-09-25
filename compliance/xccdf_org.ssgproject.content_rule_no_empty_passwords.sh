#!/bin/bash

# Title	Prevent Login to Accounts With Empty Password
# Rule	xccdf_org.ssgproject.content_rule_no_empty_passwords
# Ident	CCE-80841-0
# Remediation Source: https://github.com/ComplianceAsCode/content/blob/master/linux_os/guide/system/accounts/accounts-restrictions/password_storage/no_empty_passwords/bash/shared.sh

# platform = multi_platform_wrlinux,multi_platform_rhel,multi_platform_fedora,multi_platform_ol,multi_platform_rhv
sed --follow-symlinks -i 's/\<nullok\>//g' /etc/pam.d/system-auth
sed --follow-symlinks -i 's/\<nullok\>//g' /etc/pam.d/password-auth
