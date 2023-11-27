import { Grade } from "../entities/Grade.ts";
interface Course {
  name: string;
  teacherName: string;
  grades: Array<Grade>;
}

class GradesUtil {
  grades: Grade[];

  constructor(grades: Grade[]) {
    this.grades = grades;
  }

  convertToCourseArray() {
    const coursesMap: Record<string, Course> = {};

    for (const grade of this.grades) {
      const { id, value, text, datePublished, courseName, courseTeacher } =
        grade;

      if (!coursesMap[courseName]) {
        coursesMap[courseName] = {
          name: courseName,
          teacherName: courseTeacher,
          grades: [],
        };
      }

      coursesMap[courseName].grades.push({
        id,
        value,
        text,
        datePublished,
        courseName,
        courseTeacher,
      });
    }

    return Object.values(coursesMap);
  }

  getTotalAverage() {
    const courses = this.convertToCourseArray();
    const grades2DList = courses.map((course) =>
      course.grades.map((g) => g.value),
    );
    const averageList = grades2DList.map(
      (grades) =>
        grades.reduce(
          (accumulator, currentValue) => accumulator + currentValue,
          0,
        ) / grades.length,
    );

    return (
      averageList.reduce(
        (accumulator, currentValue) => accumulator + currentValue,
        0,
      ) / averageList.length
    );
  }
}

export default GradesUtil;
