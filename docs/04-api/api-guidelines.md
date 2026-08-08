# API Design Guidelines

- Use RESTful nouns for resources.
- Response payloads must use uniform envelope format (`ErrorResponse` for errors).
- All timestamps in ISO-8601 UTC string format.
- Validation errors must return `400 Bad Request` with field error breakdowns.
