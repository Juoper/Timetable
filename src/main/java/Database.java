public class Database {
    public static void createTables() {

        //table name: student
        //--------
        //id | name

        //table name: course
        //--------
        //id | name | subject | teacher

        //table name: lesson
        //--------
        //course_id | day | hour | room

        //table name: course_lesson
        //--------
        //course_id | lesson_id

        //table name: student_course
        //--------
        //student_id | course_id

        //table name: teacher
        //--------
        //abbreviation | prename | surname

        LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS student (id INTEGER PRIMARY KEY AUTOINCREMENT, prename TEXT, surname TEXT)");
        LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS course (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, subject TEXT, teacher TEXT)");
        LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS lesson (id INTEGER PRIMARY KEY AUTOINCREMENT, course_id INTEGER, day INTEGER, hour INTEGER, room TEXT)");
        LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS course_lesson (course_id INTEGER, lesson_id INTEGER)");
        LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS student_course (student_id INTEGER, course_id INTEGER)");
        LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS teacher (abbreviation TEXT PRIMARY KEY, prename TEXT, surname TEXT)");
    }
}
