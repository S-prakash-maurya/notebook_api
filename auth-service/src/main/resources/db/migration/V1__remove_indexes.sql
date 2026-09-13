DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'lesson'
    ) THEN

        ALTER TABLE lesson
            DROP CONSTRAINT IF EXISTS ukm8i8cshao9q95a8tipd5qwsvs;

        ALTER TABLE lesson
            ADD CONSTRAINT uk_lesson_subject_name
            UNIQUE (subject_id, name);

    END IF;
END $$;


DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'question_answer'
    ) THEN

        ALTER TABLE question_answer
            DROP CONSTRAINT IF EXISTS uk22cnuawhjjimft8uqnwswpf86;

        ALTER TABLE question_answer
            ADD CONSTRAINT uk_lesson_id_question
            UNIQUE (lesson_id, question);

    END IF;
END $$;


DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'subject_entity'
    ) THEN

        ALTER TABLE subject_entity
            DROP CONSTRAINT IF EXISTS uk6arfnea18xxroeykh3g54whla;

        ALTER TABLE subject_entity
            ADD CONSTRAINT uk_user_id_title
            UNIQUE (user_id, title);

    END IF;
END $$;