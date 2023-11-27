import {Heading, HStack, Table, TableCaption, TableContainer, Tbody, Td, Th, Thead, Tr} from "@chakra-ui/react";
import GradeDetail from "./GradeDetail.tsx";
import {Grade} from "../../entities/Grade.ts";

interface Course {
    name: string
    teacherName: string
    grades: Grade[]
}


interface Props {
    courses: Course[];
    totalAverage: number;
}

const GradesTable = ({courses, totalAverage}: Props) => {
    return (
        <TableContainer>
            <Table variant="simple">
                <TableCaption>
                    <Heading>Total Average: {totalAverage.toFixed(2)}</Heading>
                </TableCaption>
                <Thead>
                    <Tr>
                        <Th>Course</Th>
                        <Th>Teacher</Th>
                        <Th>Grades</Th>
                        <Th isNumeric>Average</Th>
                    </Tr>
                </Thead>
                <Tbody>
                    {courses.map((course) => (
                        <Tr key={course.name}>
                            <Td>
                                {course.name}
                            </Td>
                            <Td>
                                {course.teacherName}
                            </Td>
                            <Td>
                                <HStack>
                                    {course.grades.map((grade) => (
                                        <GradeDetail grade={grade} key={grade.id} />
                                    ))}
                                </HStack>
                            </Td>
                            <Td isNumeric>
                                {course.grades
                                    .map((g) => g.value)
                                    .reduce(
                                        (accumulator, currentValue) => accumulator + currentValue,
                                        0,
                                    ) / course.grades.length}
                            </Td>
                        </Tr>
                    ))}
                </Tbody>
            </Table>
        </TableContainer>
    )
}
export default GradesTable
