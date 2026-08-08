# Entity Relationship Overview

```
users (1) <---> (N) organization_members (N) <---> (1) organizations
                                                           |
                                                           v (1:N)
                                                        projects
                                                           |
                                                           v (1:N)
                                                      environments
                                                           |
                                                           v (1:N)
                                                        secrets
                                                           |
                                                           v (1:N)
                                                    secret_versions
```
