import GradesUtil from "../utils/grades-util.ts";
import useGrades from "../hooks/grade/useGrades.ts";
import ErrorMessage from "../components/shared/ErrorMessage.tsx";
import LoadingSpinner from "../components/shared/LoadingSpinner.tsx";
import GradesTable from "../components/grade/GradesTable.tsx";
import ContentPlaceholder from "../components/shared/ContentPlaceholder.tsx";

const GradesPage = () => {
  const { data: grades, isLoading, error } = useGrades();

  if (isLoading) return <LoadingSpinner />;

  if (!grades || error) return <ErrorMessage />;

  if (grades.length === 0) return <ContentPlaceholder/>

  const gradesUtil = new GradesUtil(grades);

  const courses = gradesUtil.convertToCourseArray();

  const totalAverage = gradesUtil.getTotalAverage();

  return <GradesTable courses={courses} totalAverage={totalAverage} />;
};
export default GradesPage;
