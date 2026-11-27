INSERT INTO dominio (id, tipo, chave, nome) VALUES
    (1, 'TELEFONE', 'commercial', 'Comercial'),
    (2, 'TELEFONE', 'residential', 'Residencial'),
    (3, 'TELEFONE', 'mobile', 'Celular'),
    (4, 'LOGRADOURO', 'street', 'Rua'),
    (5, 'LOGRADOURO', 'avenue', 'Avenida'),
    (6, 'LOGRADOURO', 'square', 'Praça'),
    (7, 'PAIS', 'brazil', 'Brasil'),
    (8, 'INATIVACAO', 'financial', 'Financeiro'),
    (9, 'INATIVACAO', 'contractual', 'Contratual'),
    (10, 'INATIVACAO', 'other', 'Outro'),
    (11, 'ATIVACAO', 'regularization', 'Regularização'),
    (12, 'ATIVACAO', 'contractual', 'Contratual'),
    (13, 'ATIVACAO', 'other', 'Outro')
ON CONFLICT (id) DO NOTHING;

INSERT INTO estado (sigla, nome) VALUES
    ('AC', 'Acre'), ('AL', 'Alagoas'), ('AP', 'Amapá'), ('AM', 'Amazonas'),
    ('BA', 'Bahia'), ('CE', 'Ceará'), ('DF', 'Distrito Federal'), ('ES', 'Espírito Santo'),
    ('GO', 'Goiás'), ('MA', 'Maranhão'), ('MT', 'Mato Grosso'), ('MS', 'Mato Grosso do Sul'),
    ('MG', 'Minas Gerais'), ('PA', 'Pará'), ('PB', 'Paraíba'), ('PR', 'Paraná'),
    ('PE', 'Pernambuco'), ('PI', 'Piauí'), ('RJ', 'Rio de Janeiro'), ('RN', 'Rio Grande do Norte'),
    ('RS', 'Rio Grande do Sul'), ('RO', 'Rondônia'), ('RR', 'Roraima'), ('SC', 'Santa Catarina'),
    ('SP', 'São Paulo'), ('SE', 'Sergipe'), ('TO', 'Tocantins')
ON CONFLICT (sigla) DO NOTHING;
