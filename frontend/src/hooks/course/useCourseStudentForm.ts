import {useFormik} from "formik";
import * as Yup from "yup";
import {COURSE_STUDENT_OPERATION} from "../../schemas/course-schemas.ts";

const useCourseStudentForm = (
    onSubmit: (values: {studentId: number}) => void,
) => {

    return useFormik({
        initialValues: { studentId: 0},
        validationSchema: Yup.object(COURSE_STUDENT_OPERATION),
        onSubmit
    });
};

export default useCourseStudentForm;
