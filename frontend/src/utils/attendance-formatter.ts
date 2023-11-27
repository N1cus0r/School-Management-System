import StringUtils from "./string-utils.ts";

class AttendanceFormatter {
    static getFormattedAttendancePeriod(attendancePeriod: string) {
        const [lesson, lessonNumber] = attendancePeriod.split("_");

        return StringUtils.getTitleString(lesson) + " " + lessonNumber;
    }
}

export default AttendanceFormatter
