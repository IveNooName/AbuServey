CREATE TABLE response (
  id             uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
  created_at     timestamptz NOT NULL DEFAULT now(),
  age_group      text CHECK (age_group IN ('UNDER_18','AGE_18_25','AGE_26_40','AGE_41_64','AGE_65_PLUS')),
  eligible       boolean,
  informed       smallint    NOT NULL CHECK (informed BETWEEN 1 AND 5),
  sources        text[]      NOT NULL CHECK (
                   cardinality(sources) >= 1
                   AND sources <@ ARRAY['MEDIA','SOCIAL_MEDIA','FAMILY_FRIENDS','VOTING_BOOKLET','NONE']
                   AND (NOT 'NONE' = ANY(sources) OR cardinality(sources) = 1)),
  export_opinion text        NOT NULL CHECK (export_opinion IN ('ALWAYS','STRICT_CONDITIONS','NEVER','DONT_KNOW')),
  top_argument   text        NOT NULL CHECK (top_argument   IN ('SECURITY','NEUTRALITY','WRONG_HANDS','NONE')),
  vote_intent    text        NOT NULL CHECK (vote_intent    IN ('YES','RATHER_YES','RATHER_NO','NO','UNDECIDED'))
);
