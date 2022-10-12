package org.timeTable;

public class Database {
    public static void createTables() {

        //table name: student
        //--------
        //id | name

        //table name: course
        //--------
        //id | name | subject

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
        //id | abbreviation | prename | surname

        LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS student (id INTEGER PRIMARY KEY AUTOINCREMENT, prename TEXT, surname TEXT)");
        LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS course (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE NOT NULL, subject TEXT)");
        LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS lesson (id INTEGER PRIMARY KEY AUTOINCREMENT, day INTEGER NOT NULL, hour INTEGER NOT NULL, room TEXT)");
        LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS teacher (id INTEGER PRIMARY KEY AUTOINCREMENT, abbreviation TEXT , prename TEXT, surname TEXT)");

        LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS course_lesson (course_id INTEGER, lesson_id INTEGER)");
        LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS course_teacher (course_id INTEGER, teacher_id INTEGER)");
        LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS student_course (student_id INTEGER, course_id INTEGER)");
    }
}
