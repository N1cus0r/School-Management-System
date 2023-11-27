export const CACHE_KEY_STUDENTS = ["students"]
export const CACHE_KEY_TEACHERS = ["teachers"];
export const CACHE_KEY_COURSES = ["courses"];
export const CACHE_KEY_GRADES = ["grades"];
export const CACHE_KEY_HOMEWORKS = ["homeworks"];
export const CACHE_KEY_ATTENDANCES = ["attendances"];
export const CACHE_KEY_COMMENTS = ["comments"];

export const STUDENT_NAVBAR_LINKS = [
  { label: "Grades", href: "/grades" },
  { label: "Homeworks", href: "/homeworks" },
  { label: "Attendances", href: "/attendances" },
  { label: "Comments", href: "/comments" },
];
export const TEACHER_NAVBAR_LINKS = [
  { label: "Students", href: "/students" },
  { label: "Courses", href: "/courses" },
];
export const ADMIN_NAVBAR_LINKS = [
  { label: "Teachers", href: "/teachers" },
  ...TEACHER_NAVBAR_LINKS,
];
