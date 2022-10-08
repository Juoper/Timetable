public class Database {
    public static void createTables() {

        //TODO
        //table name: student
        //--------
        //id | name

        //table name: course
        //--------
        //id | name | room | teacher

        //table name: lessons
        //--------
        //

        //table name: student_course
        //--------
        //student_id | course_id

        //before doing this one TODO in Lesson.java has to be done
        //table name: lesson
        //--------
        //course_id | day | hour

        //table name: teacher
        //--------
        //abbreviation | name

        LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS student (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)");
        LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS course (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, room TEXT, teacher INTEGER, day INTEGER, hour INTEGER)");
    }
}
