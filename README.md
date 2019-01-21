# itimalia
Backend application for an animal shelter

Requisitos:

1 - O usuario pode se cadastrar na plataforma informando email, senha, data de nascimento, gênero, nome e telefone. O retorno será esses mesmos campos junto com a data de criação e ultima modificação;
2 - Um usuario pode ser administrador ou nao. Um administrador pode cadastrar ou modificar um outro usuario. O usuario sem poderes administrativos so pode mudar o seu cadastro. Modificando, alterar a data de modificacao. Apenas administradores podem adicionar outros administradores
3 - Um adminstrador pode cadastrar novos animais pro abrigo;
4 - O animal pode ser cadastrado através de um apelido, data de nascimento (opcional), espécie e raça (opcional). Ao cadastrar, retornar os mesmos campos junto com a data de criação e o status (disponivel para adoção, falecido, adotado). O administrador pode modificar todos os campos.
5 - Um usuário pode adotar um animal através de um post para o id do animal. A requisição terá sucesso (200) se o animal estiver disponível para adoção. Ela retornará 404 se não houver animal com o id, 401 se foi adotado e 403 se ele faleceu;


Desejáveis:

1 - Informar quem adotou o animal ao requisitá-lo
2 - Listar os animais adotados por pessoa
3 - Listar os animais por status
4 - Remover usuários
5 - Login de usuarios
6 - Uso de tokens com tempo de acesso
7 - Uso de JWT
