# Envelope Encryption Strategy

1. **Data Encryption Key (DEK)**: Unique key generated per secret version to encrypt secret value using AES-256-GCM.
2. **Key Encryption Key (KEK)**: Master key stored in environment config (`ENCRYPTION_KEY`) used to encrypt the DEK.
3. Plaintext secret is never stored in DB; DEK is stored encrypted alongside the secret payload.
