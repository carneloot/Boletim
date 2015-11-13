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

        private DB() {}
    }

    private Constants() {}
}
