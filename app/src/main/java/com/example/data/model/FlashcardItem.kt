package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcard_items")
data class FlashcardItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String, // "Physics", "Chemistry", "Mathematics", "Computer Science"
    val frontTitle: String,
    val frontPrompt: String,
    val backAnswer: String,
    val formulaOrKeySteps: String,
    val isCustom: Boolean = false,
    val isMastered: Boolean = false,
    val timesReviewed: Int = 0
)

object DefaultFlashcards {
    val items: List<FlashcardItem> = listOf(
        // Physics
        FlashcardItem(
            subject = "Physics",
            frontTitle = "Terminal Velocity (Stokes' Law)",
            frontPrompt = "State Stokes' Law and derive the formula for terminal velocity of a spherical body falling in a viscous medium.",
            backAnswer = "Viscous force F = 6πηrv. At terminal velocity, Downward force (Gravity) = Upward force (Buoyancy + Viscosity).",
            formulaOrKeySteps = "v_t = \\frac{2}{9} \\frac{r^2(\\rho - \u03c3)g}{\\eta}"
        ),
        FlashcardItem(
            subject = "Physics",
            frontTitle = "Torque & Moment of Inertia",
            frontPrompt = "Show relation between Torque (τ) and Moment of Inertia (I).",
            backAnswer = "For rigid rotating body with particles of mass m_i at distance r_i:\nτ = Σ (m_i * r_i^2) * α = Iα.",
            formulaOrKeySteps = "\u03c4 = I \u03b1, \\quad L = I \u03c9"
        ),
        FlashcardItem(
            subject = "Physics",
            frontTitle = "Lens Maker's Formula",
            frontPrompt = "State Lens Maker's Formula and conditions for thin biconvex lens.",
            backAnswer = "Refraction at two spherical surfaces combined yields:\n1/f = (μ - 1)(1/R1 - 1/R2).",
            formulaOrKeySteps = "\\frac{1}{f} = (\\mu - 1)\\left(\\frac{1}{R_1} - \\frac{1}{R_2}\\right)"
        ),
        FlashcardItem(
            subject = "Physics",
            frontTitle = "Simple Harmonic Motion (Pendulum)",
            frontPrompt = "Derive equation of time period of simple pendulum.",
            backAnswer = "Restoring torque τ = -mg l sin(θ) ≈ -mgl θ for small angle. Angular acceleration d²θ/dt² = -(g/l)θ.",
            formulaOrKeySteps = "T = 2\\pi \\sqrt{\\frac{l}{g}}"
        ),
        FlashcardItem(
            subject = "Physics",
            frontTitle = "Carnot Cycle Efficiency",
            frontPrompt = "What is the theoretical maximum efficiency of a Carnot heat engine?",
            backAnswer = "Operates on 2 isothermal & 2 adiabatic reversible processes between T1 (source) and T2 (sink).",
            formulaOrKeySteps = "\\eta = 1 - \\frac{T_2}{T_1} = \\frac{W}{Q_1}"
        ),

        // Chemistry
        FlashcardItem(
            subject = "Chemistry",
            frontTitle = "Grignard Reagent + Formaldehyde",
            frontPrompt = "What is the product when RMgX reacts with Formaldehyde (HCHO) followed by acid hydrolysis?",
            backAnswer = "Nucleophilic addition of R⁻ to carbonyl carbon followed by hydrolysis gives a Primary (1°) Alcohol: R-CH2-OH + Mg(OH)X.",
            formulaOrKeySteps = "HCHO + RMgX \\rightarrow R-CH_2-OMgX \\xrightarrow{H_3O^+} R-CH_2-OH + Mg(OH)X"
        ),
        FlashcardItem(
            subject = "Chemistry",
            frontTitle = "Aldol Condensation",
            frontPrompt = "What are the essential conditions for Aldol Condensation?",
            backAnswer = "Must possess at least one α-hydrogen atom in presence of dilute alkali (NaOH). Two aldehyde/ketone molecules form a β-hydroxy aldehyde, dehydrating upon heating to α,β-unsaturated carbonyl.",
            formulaOrKeySteps = "2 CH_3CHO \\xrightarrow{dil. NaOH} CH_3-CH(OH)-CH_2-CHO \\xrightarrow{\\Delta} CH_3-CH=CH-CHO + H_2O"
        ),
        FlashcardItem(
            subject = "Chemistry",
            frontTitle = "SN1 vs SN2 Mechanism",
            frontPrompt = "Compare rate kinetics and stereochemistry of SN1 vs SN2.",
            backAnswer = "SN1: 2-step via planar carbocation intermediate, 1st order kinetics, racemization. Favored by 3° halides.\nSN2: 1-step concerted backside attack via pentavalent transition state, 2nd order kinetics, Walden inversion. Favored by 1° halides.",
            formulaOrKeySteps = "SN1: Rate = k[R-X] (Racemic)\nSN2: Rate = k[R-X][Nu^-] (Inversion)"
        ),
        FlashcardItem(
            subject = "Chemistry",
            frontTitle = "Nernst Equation",
            frontPrompt = "Write the Nernst equation for a cell reaction at 298 K.",
            backAnswer = "Relates cell potential E_cell to standard cell potential E°_cell and reaction quotient Q.",
            formulaOrKeySteps = "E_{cell} = E^\u2218_{cell} - \\frac{0.0591}{n} \\log_{10}(Q)"
        ),

        // Mathematics
        FlashcardItem(
            subject = "Mathematics",
            frontTitle = "Standard Integral 1/(x² + a²)",
            frontPrompt = "State standard integral of 1 / (x² + a²) and substitution method.",
            backAnswer = "Let x = a tan θ, dx = a sec² θ dθ. Integral reduces to (1/a) θ + C.",
            formulaOrKeySteps = "\\int \\frac{dx}{x^2 + a^2} = \\frac{1}{a} \\arctan\\left(\\frac{x}{a}\\right) + C"
        ),
        FlashcardItem(
            subject = "Mathematics",
            frontTitle = "Integration by Parts (ILATE)",
            frontPrompt = "State formula for integration of product of two functions.",
            backAnswer = "Choose u using ILATE (Inverse, Log, Algebraic, Trig, Exponential).",
            formulaOrKeySteps = "\\int u v \\, dx = u \\int v \\, dx - \\int \\left( \\frac{du}{dx} \\int v \\, dx \\right) dx"
        ),
        FlashcardItem(
            subject = "Mathematics",
            frontTitle = "Lagrange's Mean Value Theorem",
            frontPrompt = "State LMVT conditions and conclusion for f(x) on [a, b].",
            backAnswer = "If f(x) is continuous on [a, b] and differentiable on (a, b), there exists at least one c ∈ (a, b) such that:",
            formulaOrKeySteps = "f'(c) = \\frac{f(b) - f(a)}{b - a}"
        ),
        FlashcardItem(
            subject = "Mathematics",
            frontTitle = "Derivative of Sin(x) from First Principles",
            frontPrompt = "Write initial first principles definition for f(x) = sin x.",
            backAnswer = "f'(x) = lim_{h->0} [sin(x+h) - sin(x)] / h = lim 2 cos(x + h/2) sin(h/2) / h = cos x.",
            formulaOrKeySteps = "f'(x) = \\lim_{h \\to 0} \\frac{f(x+h) - f(x)}{h} = \\cos(x)"
        ),

        // Computer Science
        FlashcardItem(
            subject = "Computer Science",
            frontTitle = "Database Normalization (1NF, 2NF, 3NF)",
            frontPrompt = "Explain requirements for 1NF, 2NF, and 3NF.",
            backAnswer = "1NF: Atomic attribute values (no repeating groups).\n2NF: In 1NF and no partial dependencies (all non-key attributes fully depend on candidate key).\n3NF: In 2NF and no transitive dependencies (X -> Y -> Z).",
            formulaOrKeySteps = "1NF (Atomicity) \u2192 2NF (No Partial Key Dep) \u2192 3NF (No Transitive Dep)"
        ),
        FlashcardItem(
            subject = "Computer Science",
            frontTitle = "Object-Oriented Programming (OOP Pillars)",
            frontPrompt = "Name and explain the 4 fundamental pillars of OOP.",
            backAnswer = "Encapsulation (bundling data and methods, data hiding), Abstraction (hiding implementation complexity), Inheritance (reusability & hierarchy), Polymorphism (method overloading & overriding).",
            formulaOrKeySteps = "1. Encapsulation  2. Abstraction  3. Inheritance  4. Polymorphism"
        )
    )
}
