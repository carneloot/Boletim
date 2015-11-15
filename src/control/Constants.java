package control;


public final class Constants
{
    public static final String LOOK_AND_FEEL = "Windows";

    public final class Properties
    {
        public static final String ARQUIVO = "boletim.properties";

        public static final String COMENTARIO = "Configuracoes Boletim";

        public final class Keys
        {
            public static final String TIPO = "tipo";
            public static final String PESO_PROVA = "peso_prova";
            public static final String PESO_TRABALHO = "peso_trabalho";
            public static final String PESO_TAREFA = "peso_tarefa";
            public static final String LAST_NAME = "last_name";
            public static final String MEDIA = "media";
            public static final String PERIODO = "periodo";

            private Keys() {}
        }

        public final class Values
        {

            public final class Tipo
            {
                public static final String ACUMULATIVO = "acumulativo"; // Padrao
                public static final String PORCENTAGEM = "porcentagem";

                private Tipo() {}
            }

            public final class Periodo
            {
                public static final String BIMESTRE = "bimestre";
                public static final String TRIMESTRE = "trimestre";
                public static final String SEMESTRE = "semestre";

                private Periodo() {}
            }

            private Values() {}
        }

        private Properties() {}
    }

    public final class DB
    {
        public static final String ARQUIVO = "boletim.db";

        public static final String CREATE_DATABASE = "BEGIN TRANSACTION;\n" +
                "CREATE TABLE `Notas` (\n" +
                "\t`NotCodigo`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`NotDisciplina`\tINTEGER NOT NULL,\n" +
                "\t`NotNota`\tNUMERIC NOT NULL CHECK(NotNota >= 0 AND NotNota <= 10),\n" +
                "\t`NotPeriodo`\tINTEGER NOT NULL CHECK(NotPeriodo >= 1 AND NotPeriodo <= 4)\n" +
                ");\n" +
                "CREATE TABLE \"Disciplinas\" (\n" +
                "\t`DisCodigo`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`DisNome`\tTEXT NOT NULL,\n" +
                "\t`DisProfessor`\tTEXT NOT NULL,\n" +
                "\t`DisAtivado`\tTEXT NOT NULL DEFAULT 'S'\n" +
                ");\n" +
                "COMMIT;\n";

        private DB() {}
    }

    public final class Mensagens
    {
        public static final String ERRO_SOMA_PESOS = "A soma dos pesos nao corresponde a 10,0.\nPor favor, corrija o erro.";

        public static final String MUDANCA_DE_PERIODO = "Atencao!\nAo modificar essa opcao todos dos dados de notas serao resetados.";

        public static final String AVISO_REMOVER_NOTA = "Tem certeza que deseja remover essa nota?";

        public static final String AVISO_REMOVER_DISCIPLINA = "Tem certeza que deseja remover essa disciplina?";

        public static final String CAMPO_PREENCHIDO_AUTOMATICAMENTE = "Esse campo eh preenchido automaticamente.";

        public static final String NOTA_JA_PREENCHIDA = "Essa nota ja foi preenchida.\nPor favor, exclua a nota para poder adicionar.";

        public static final String NOTA_NAO_PREENCHIDA = "Essa nota ainda nao foi preenchida.\nPor favor, escolha outra nota.";

        public static final String FUNCAO_NAO_IMPLEMENTADA = "Essa funcao ainda nao está implementada.";

        private Mensagens() {}
    }

    private Constants() {}
}
